package cn.meiracle.aigc.service.strategy.impl;

import cn.meiracle.aigc.common.exception.MediaException;
import cn.meiracle.aigc.domain.bo.GenerateImageBo;
import cn.meiracle.aigc.domain.bo.GenerateVideoBo;
import cn.meiracle.aigc.domain.vo.GenerateResultVo;
import cn.meiracle.aigc.service.AiUsageRecordService;
import cn.meiracle.aigc.service.strategy.MediaGenStrategy;
import cn.meiracle.http.client.HttpTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 可灵（Kling）策略实现
 * 注意：可灵暂无官方 Java SDK，通过 meiracle-common 的 http 能力（HttpTemplate）直连 API。
 *
 * <p>统一 HTTP 调用收敛到 cn.meiracle.http.HttpTemplate，不在本模块内 new RestTemplate / HttpClient，
 * 便于超时、拦截、可观测等能力在公共层统一治理。</p>
 *
 * 鉴权方式：控制台 API Key 直接放 Authorization: Bearer 头里（无需 JWT 签名）
 * 官方文档：https://app.klingai.com/cn/dev/document-api
 *
 * @author meiracle
 */
@Slf4j
@Component
public class KlingStrategy implements MediaGenStrategy {

    /**
     * 任务类型标记 —— 可灵查询任务时必须区分类型，没有通用的查询端点。
     * 提交任务时把类型编码进返回的 taskId（形如 text2video:1234567890），
     * 这样轮询时无需依赖进程内状态即可还原正确的查询路径。
     */
    private static final String TYPE_TEXT2VIDEO = "text2video";
    private static final String TYPE_IMAGE2VIDEO = "image2video";
    private static final String TYPE_IMAGE = "image";

    /**
     * 任务类型 -> 查询路径前缀。可灵查询端点与创建端点同路径、仅 HTTP 方法不同；
     * 视频是 /v1/videos/{类型}/{task_id}，而图片是 /v1/images/generations/{task_id}，
     * 两者命名不一致，故单独维护映射。
     */
    private static final Map<String, String> QUERY_PATH_PREFIX = Map.of(
        TYPE_TEXT2VIDEO, "/v1/videos/text2video/",
        TYPE_IMAGE2VIDEO, "/v1/videos/image2video/",
        TYPE_IMAGE, "/v1/images/generations/"
    );

    private final HttpTemplate httpTemplate;
    private final AiUsageRecordService usageRecordService;

    public KlingStrategy(HttpTemplate httpTemplate, AiUsageRecordService usageRecordService) {
        this.httpTemplate = httpTemplate;
        this.usageRecordService = usageRecordService;
    }

    @Value("${media.kling.api-key:}")
    private String apiKey;

    /**
     * 国内域名 api-beijing.klingai.com，国际站 api-singapore.klingai.com
     */
    @Value("${media.kling.base-url:https://api-beijing.klingai.com}")
    private String baseUrl;

    @Value("${media.kling.default-image-model:kling-v2-1}")
    private String defaultImageModel;

    @Value("${media.kling.default-video-model:kling-3.0-omni}")
    private String defaultVideoModel;

    @Override
    public String getProvider() {
        return "kling";
    }

    @Override
    public GenerateResultVo generateImage(GenerateImageBo bo) {
        log.info("[可灵] 文生图开始, prompt: {}", bo.getPrompt());

        String model = bo.getModel() != null ? bo.getModel() : defaultImageModel;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model_name", model);
        requestBody.put("prompt", bo.getPrompt());
        requestBody.put("aspect_ratio", bo.getAspectRatio());
        requestBody.put("n", bo.getN());
        if (bo.getNegativePrompt() != null && !bo.getNegativePrompt().isBlank()) {
            requestBody.put("negative_prompt", bo.getNegativePrompt());
        }

        return submitTask("/v1/images/generations", requestBody, TYPE_IMAGE, "文生图");
    }

    @Override
    public GenerateResultVo generateImageWithRef(GenerateImageBo bo) {
        String refImage = bo.getReferenceImageUrl();
        if (refImage == null || refImage.isBlank()) {
            log.warn("[可灵] 参考图生图缺少 referenceImageUrl, 降级为文生图");
            return generateImage(bo);
        }
        log.info("[可灵] 参考图生图开始, ref: {}", refImage);

        String model = bo.getModel() != null ? bo.getModel() : defaultImageModel;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model_name", model);
        requestBody.put("prompt", bo.getPrompt());
        requestBody.put("image", refImage);
        requestBody.put("aspect_ratio", bo.getAspectRatio());
        requestBody.put("n", bo.getN());
        if (bo.getNegativePrompt() != null && !bo.getNegativePrompt().isBlank()) {
            requestBody.put("negative_prompt", bo.getNegativePrompt());
        }

        return submitTask("/v1/images/generations", requestBody, TYPE_IMAGE, "参考图生图");
    }

    @Override
    public GenerateResultVo generateVideo(GenerateVideoBo bo) {
        log.info("[可灵] 文生视频开始, prompt: {}, duration: {}s", bo.getPrompt(), bo.getDuration());

        String model = bo.getModel() != null ? bo.getModel() : defaultVideoModel;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model_name", model);
        requestBody.put("prompt", bo.getPrompt());
        requestBody.put("mode", "std");
        // 官方文档中 duration 为字符串
        requestBody.put("duration", String.valueOf(bo.getDuration()));
        requestBody.put("aspect_ratio", bo.getAspectRatio());
        // 可灵视频端点用 sound("on"/"off") 控制音频，而非 generate_audio
        requestBody.put("sound", Boolean.TRUE.equals(bo.getGenerateAudio()) ? "on" : "off");
        if (bo.getNegativePrompt() != null && !bo.getNegativePrompt().isBlank()) {
            requestBody.put("negative_prompt", bo.getNegativePrompt());
        }

        return submitTask("/v1/videos/text2video", requestBody, TYPE_TEXT2VIDEO, "文生视频");
    }

    @Override
    public GenerateResultVo generateVideoWithImage(GenerateVideoBo bo) {
        log.info("[可灵] 图生视频开始, firstFrame: {}", bo.getFirstFrameUrl());

        String model = bo.getModel() != null ? bo.getModel() : defaultVideoModel;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model_name", model);
        requestBody.put("prompt", bo.getPrompt());
        requestBody.put("mode", "std");
        requestBody.put("duration", String.valueOf(bo.getDuration()));
        requestBody.put("aspect_ratio", bo.getAspectRatio());
        requestBody.put("sound", Boolean.TRUE.equals(bo.getGenerateAudio()) ? "on" : "off");
        if (bo.getFirstFrameUrl() != null && !bo.getFirstFrameUrl().isBlank()) {
            requestBody.put("image", bo.getFirstFrameUrl());
        }
        if (bo.getLastFrameUrl() != null && !bo.getLastFrameUrl().isBlank()) {
            requestBody.put("image_tail", bo.getLastFrameUrl());
        }

        return submitTask("/v1/videos/image2video", requestBody, TYPE_IMAGE2VIDEO, "图生视频");
    }

    @Override
    public GenerateResultVo queryTaskStatus(String taskId) {
        log.info("[可灵] 查询任务状态: {}", taskId);

        // 还原提交时编码的任务类型与模型，缺失时按文生视频处理（兼容历史数据）
        String taskType = TYPE_TEXT2VIDEO;
        String model = defaultVideoModel;
        String realTaskId = taskId;
        // 新格式 type:model:taskId；旧格式 type:taskId（无 model）
        String[] parts = taskId.split(":", 3);
        if (parts.length == 3) {
            taskType = parts[0];
            model = parts[1];
            realTaskId = parts[2];
        } else if (parts.length == 2) {
            taskType = parts[0];
            realTaskId = parts[1];
        }
        String pathPrefix = QUERY_PATH_PREFIX.getOrDefault(taskType,
            QUERY_PATH_PREFIX.get(TYPE_TEXT2VIDEO));

        GenerateResultVo vo = new GenerateResultVo();
        vo.setTaskId(taskId);

        try {
            // 可灵查询端点与创建端点同路径、仅 HTTP 方法不同；通过公共 http 能力发起 GET
            String url = baseUrl + pathPrefix + realTaskId;
            JsonNode root = httpTemplate.getForObject(url, buildAuthHeaders(), JsonNode.class);

            String bizError = checkBizCode(root);
            if (bizError != null) {
                vo.setStatus("FAILED");
                vo.setFailReason(bizError);
                return vo;
            }

            JsonNode data = root.path("data");
            // 终态是 succeed（不是 succeeded），submitted/processing 均视为进行中
            String taskStatus = data.path("task_status").asText("");

            switch (taskStatus.toLowerCase()) {
                case "succeed" -> {
                    vo.setStatus("SUCCESS");
                    vo.setUrls(extractResultUrls(data.path("task_result")));
                    // 任务成功时记一次用量，以真实 taskId 幂等，避免轮询重复计费
                    recordUsageOnSuccess(taskType, model, realTaskId, data.path("task_result"));
                }
                case "failed" -> {
                    vo.setStatus("FAILED");
                    vo.setFailReason(data.path("task_status_msg").asText("生成失败"));
                }
                default -> vo.setStatus("RUNNING");
            }

            return vo;
        } catch (Exception e) {
            log.error("[可灵] 查询任务状态失败, taskId: {}", taskId, e);
            vo.setStatus("FAILED");
            vo.setFailReason(e.getMessage());
            return vo;
        }
    }

    /**
     * 任务成功时记录用量
     * <p>图片任务按张数计费，视频任务按秒数计费；以真实 taskId 幂等，轮询命中多次只记一条。
     */
    private void recordUsageOnSuccess(String taskType, String model, String realTaskId, JsonNode taskResult) {
        try {
            if (TYPE_IMAGE.equals(taskType)) {
                int imageCount = taskResult.path("images").size();
                if (imageCount == 0) {
                    log.debug("[可灵] 任务结果无图片，跳过用量记录, taskId={}", realTaskId);
                    return;
                }
                usageRecordService.recordUsageOnce("kling", model,
                    AiUsageRecordService.USAGE_TYPE_IMAGE_COUNT,
                    BigDecimal.valueOf(imageCount), null, realTaskId);
            } else {
                // 视频时长取 task_result.videos[].duration（可灵返回字符串秒数），缺失时跳过
                BigDecimal seconds = sumVideoSeconds(taskResult);
                if (seconds == null) {
                    log.debug("[可灵] 任务结果无时长，跳过用量记录, taskId={}", realTaskId);
                    return;
                }
                usageRecordService.recordUsageOnce("kling", model,
                    AiUsageRecordService.USAGE_TYPE_VIDEO_SECONDS,
                    seconds, null, realTaskId);
            }
        } catch (Exception e) {
            log.warn("[可灵] 记录用量失败，已忽略, taskId={}", realTaskId, e);
        }
    }

    /**
     * 汇总视频时长（秒）。可灵 videos[].duration 为字符串，缺失时返回 null
     */
    private BigDecimal sumVideoSeconds(JsonNode taskResult) {
        BigDecimal total = BigDecimal.ZERO;
        boolean found = false;
        for (JsonNode video : taskResult.path("videos")) {
            String duration = video.path("duration").asText("");
            if (!duration.isBlank()) {
                try {
                    total = total.add(new BigDecimal(duration));
                    found = true;
                } catch (NumberFormatException ignored) {
                    // 单条时长异常不影响整体统计
                }
            }
        }
        return found ? total : null;
    }

    /**
     * 从请求体取模型名，用于编码进 taskId
     */
    private String modelName(Map<String, Object> body) {
        Object modelName = body.get("model_name");
        return modelName == null ? "" : modelName.toString();
    }

    /**
     * 提交生成任务（经公共 http 能力发起 POST JSON）
     *
     * @param path     创建任务的路径
     * @param body     请求体
     * @param taskType 任务类型，用于后续拼查询路径
     * @param action   日志描述
     */
    private GenerateResultVo submitTask(String path, Map<String, Object> body, String taskType, String action) {
        GenerateResultVo vo = new GenerateResultVo();

        try {
            JsonNode root = httpTemplate.postJsonForObject(baseUrl + path, buildAuthHeaders(), body, JsonNode.class);

            // 可灵业务错误以 HTTP 200 + code != 0 返回，必须校验，否则会拿到空 taskId 且状态误判为 PENDING
            String bizError = checkBizCode(root);
            if (bizError != null) {
                log.error("[可灵] {}提交失败: {}", action, bizError);
                vo.setStatus("FAILED");
                vo.setFailReason(bizError);
                return vo;
            }

            String realTaskId = root.path("data").path("task_id").asText("");
            if (realTaskId.isBlank()) {
                vo.setStatus("FAILED");
                vo.setFailReason("可灵未返回 task_id");
                return vo;
            }

            log.info("[可灵] {}任务已提交, taskId: {}", action, realTaskId);
            // 编码类型与模型：可灵查询任务的响应里不回传模型，轮询时需要靠它补全用量记录的 model
            vo.setTaskId(taskType + ":" + modelName(body) + ":" + realTaskId);
            vo.setStatus("PENDING");
            return vo;
        } catch (Exception e) {
            log.error("[可灵] {}失败", action, e);
            vo.setStatus("FAILED");
            vo.setFailReason(e.getMessage());
            return vo;
        }
    }

    /**
     * 校验可灵统一响应包络中的业务码
     *
     * @return 业务错误描述；无错误返回 null
     */
    private String checkBizCode(JsonNode root) {
        int code = root.path("code").asInt(0);
        if (code != 0) {
            return String.format("可灵返回错误 code=%d, message=%s",
                code, root.path("message").asText("未知错误"));
        }
        return null;
    }

    /**
     * 从 task_result 中提取结果 URL，视频取 videos、图片取 images
     */
    private List<String> extractResultUrls(JsonNode taskResult) {
        List<String> urls = new ArrayList<>();
        if (taskResult.isMissingNode() || taskResult.isNull()) {
            return urls;
        }
        for (String field : List.of("videos", "images")) {
            taskResult.path(field).forEach(item -> {
                String url = item.path("url").asText(null);
                if (url != null && !url.isBlank()) {
                    urls.add(url);
                }
            });
        }
        return urls;
    }

    /**
     * 构建鉴权 Header（可灵直接 API Key Bearer Token）
     */
    private Map<String, String> buildAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        return headers;
    }

}
