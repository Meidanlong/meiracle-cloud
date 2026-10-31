package cn.meiracle.aigc.service.strategy.impl;

import cn.meiracle.aigc.domain.bo.GenerateImageBo;
import cn.meiracle.aigc.domain.bo.GenerateVideoBo;
import cn.meiracle.aigc.domain.vo.GenerateResultVo;
import cn.meiracle.aigc.service.AiUsageRecordService;
import cn.meiracle.aigc.service.strategy.MediaGenStrategy;
import com.volcengine.ark.runtime.model.content.generation.*;
import com.volcengine.ark.runtime.model.images.generation.*;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 火山引擎方舟（即梦）策略实现
 * 使用官方 Java SDK：volcengine-java-sdk-ark-runtime（生图/生视频走 SDK，不走裸 HTTP）。
 *
 * <p>说明：本策略调用三方平台的方式是官方 SDK 而非裸 HTTP，因此不经由 cn.meiracle.http.HttpTemplate；
 * 仅可灵这类无官方 SDK 的厂商才在 KlingStrategy 中统一走公共 http 能力。</p>
 *
 * @author meiracle
 */
@Slf4j
@Component
public class VolcanoArkStrategy implements MediaGenStrategy {

    /**
     * 画幅比例 -> 宽高数值，用于把分辨率档位换算成方舟 size 像素值
     */
    private static final Map<String, int[]> AREA_BY_ASPECT = Map.of(
        "9:16", new int[]{9, 16},
        "16:9", new int[]{16, 9},
        "1:1", new int[]{1, 1},
        "3:4", new int[]{3, 4},
        "4:3", new int[]{4, 3},
        "4:5", new int[]{4, 5},
        "5:4", new int[]{5, 4},
        "21:9", new int[]{21, 9}
    );

    private final AiUsageRecordService usageRecordService;

    public VolcanoArkStrategy(AiUsageRecordService usageRecordService) {
        this.usageRecordService = usageRecordService;
    }

    @Value("${media.ark.api-key:}")
    private String apiKey;

    @Value("${media.ark.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${media.ark.default-image-model:doubao-seedream-5-0-flash-260915}")
    private String defaultImageModel;

    @Value("${media.ark.default-video-model:doubao-seedance-2-5-260628}")
    private String defaultVideoModel;

    private ArkService arkService;

    /**
     * 初始化 ArkService 客户端
     */
    @PostConstruct
    public void init() {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.MINUTES);
        Dispatcher dispatcher = new Dispatcher();
        this.arkService = ArkService.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .build();
        log.info("[火山引擎] ArkService 初始化完成, baseUrl: {}", baseUrl);
    }

    @PreDestroy
    public void destroy() {
        if (arkService != null) {
            arkService.shutdownExecutor();
        }
    }

    @Override
    public String getProvider() {
        return "volcano";
    }

    @Override
    public GenerateResultVo generateImage(GenerateImageBo bo) {
        log.info("[火山引擎] 文生图开始, prompt: {}, model: {}", bo.getPrompt(), defaultImageModel);

        String model = bo.getModel() != null ? bo.getModel() : defaultImageModel;

        GenerateImagesRequest request = GenerateImagesRequest.builder()
            .model(model)
            .prompt(bo.getPrompt())
            .size(normalizeSize(bo.getAspectRatio(), bo.getResolution()))
            .responseFormat(ResponseFormat.Url)
            .watermark(false)
            .build();

        ImagesResponse response = arkService.generateImages(request);
        List<String> urls = response.getData().stream()
            .map(ImagesResponse.Image::getUrl)
            .filter(url -> url != null && !url.isBlank())
            .toList();

        log.info("[火山引擎] 文生图完成, 生成 {} 张图片", urls.size());

        // 同步接口已直接出图，按返回的 token 消耗计费（无 taskId）
        recordImageUsage(model, response);

        GenerateResultVo result = new GenerateResultVo();
        result.setStatus("SUCCESS");
        result.setUrls(urls);
        return result;
    }

    /**
     * 记录一次文生图的 token 消耗
     * <p>同步生图没有 taskId，无法做幂等；用量统计为旁路，失败不影响出图结果。
     */
    private void recordImageUsage(String model, ImagesResponse response) {
        try {
            Integer totalTokens = response.getUsage() == null ? null : response.getUsage().getTotalTokens();
            if (totalTokens == null) {
                log.debug("[火山引擎] 响应未返回 totalTokens，跳过用量记录, model={}", model);
                return;
            }
            usageRecordService.recordUsage("volcano", model, AiUsageRecordService.USAGE_TYPE_TOKEN,
                BigDecimal.valueOf(totalTokens), null, null);
        } catch (Exception e) {
            log.warn("[火山引擎] 记录文生图用量失败，已忽略, model={}", model, e);
        }
    }

    @Override
    public GenerateResultVo generateImageWithRef(GenerateImageBo bo) {
        String refImage = bo.getReferenceImageUrl();
        if (refImage == null || refImage.isBlank()) {
            log.warn("[火山引擎] 参考图生图缺少 referenceImageUrl, 降级为文生图");
            return generateImage(bo);
        }
        log.info("[火山引擎] 参考图生图开始, ref: {}", refImage);

        String model = bo.getModel() != null ? bo.getModel() : defaultImageModel;

        // 传入参考图，SDK 接受单个 URL 或 URL 列表（最多 10 张）
        GenerateImagesRequest request = GenerateImagesRequest.builder()
            .model(model)
            .prompt(bo.getPrompt())
            .image(refImage)
            .size(normalizeSize(bo.getAspectRatio(), bo.getResolution()))
            .responseFormat(ResponseFormat.Url)
            .watermark(false)
            .build();

        ImagesResponse response = arkService.generateImages(request);
        List<String> urls = response.getData().stream()
            .map(ImagesResponse.Image::getUrl)
            .filter(url -> url != null && !url.isBlank())
            .toList();

        log.info("[火山引擎] 参考图生图完成, 生成 {} 张图片", urls.size());

        recordImageUsage(model, response);

        GenerateResultVo result = new GenerateResultVo();
        result.setStatus("SUCCESS");
        result.setUrls(urls);
        return result;
    }

    @Override
    public GenerateResultVo generateVideo(GenerateVideoBo bo) {
        log.info("[火山引擎] 文生视频开始, prompt: {}, duration: {}s, ratio: {}",
            bo.getPrompt(), bo.getDuration(), bo.getAspectRatio());

        String model = bo.getModel() != null ? bo.getModel() : defaultVideoModel;

        // 构建内容列表
        List<CreateContentGenerationTaskRequest.Content> contents = new ArrayList<>();
        contents.add(CreateContentGenerationTaskRequest.Content.builder()
            .type("text")
            .text(bo.getPrompt())
            .build());

        // 构建请求
        CreateContentGenerationTaskRequest request = CreateContentGenerationTaskRequest.builder()
            .model(model)
            .content(contents)
            .duration(bo.getDuration().longValue())
            .ratio(bo.getAspectRatio())
            .watermark(false)
            .generateAudio(bo.getGenerateAudio())
            .build();

        // 提交任务
        CreateContentGenerationTaskResult result = arkService.createContentGenerationTask(request);
        log.info("[火山引擎] 视频任务已提交, taskId: {}", result.getId());

        GenerateResultVo vo = new GenerateResultVo();
        vo.setTaskId(result.getId());
        vo.setStatus("PENDING");
        return vo;
    }

    @Override
    public GenerateResultVo generateVideoWithImage(GenerateVideoBo bo) {
        log.info("[火山引擎] 图生视频开始, firstFrame: {}", bo.getFirstFrameUrl());

        String model = bo.getModel() != null ? bo.getModel() : defaultVideoModel;

        List<CreateContentGenerationTaskRequest.Content> contents = new ArrayList<>();
        // 文本提示词
        contents.add(CreateContentGenerationTaskRequest.Content.builder()
            .type("text")
            .text(bo.getPrompt())
            .build());
        // 首帧图，role 必须为 first_frame（官方图生视频约定）
        contents.add(CreateContentGenerationTaskRequest.Content.builder()
            .type("image_url")
            .role("first_frame")
            .imageUrl(CreateContentGenerationTaskRequest.ImageUrl.builder()
                .url(bo.getFirstFrameUrl())
                .build())
            .build());
        // 尾帧图（首尾帧模式），role 必须为 last_frame
        if (bo.getLastFrameUrl() != null && !bo.getLastFrameUrl().isBlank()) {
            contents.add(CreateContentGenerationTaskRequest.Content.builder()
                .type("image_url")
                .role("last_frame")
                .imageUrl(CreateContentGenerationTaskRequest.ImageUrl.builder()
                    .url(bo.getLastFrameUrl())
                    .build())
                .build());
        }

        CreateContentGenerationTaskRequest request = CreateContentGenerationTaskRequest.builder()
            .model(model)
            .content(contents)
            .duration(bo.getDuration().longValue())
            .ratio(bo.getAspectRatio())
            .watermark(false)
            .generateAudio(bo.getGenerateAudio())
            .build();

        CreateContentGenerationTaskResult result = arkService.createContentGenerationTask(request);
        log.info("[火山引擎] 图生视频任务已提交, taskId: {}", result.getId());

        GenerateResultVo vo = new GenerateResultVo();
        vo.setTaskId(result.getId());
        vo.setStatus("PENDING");
        return vo;
    }

    @Override
    public GenerateResultVo queryTaskStatus(String taskId) {
        log.info("[火山引擎] 查询任务状态: {}", taskId);

        GetContentGenerationTaskRequest request = GetContentGenerationTaskRequest.builder()
            .taskId(taskId)
            .build();

        GetContentGenerationTaskResponse response = arkService.getContentGenerationTask(request);

        GenerateResultVo vo = new GenerateResultVo();
        vo.setTaskId(taskId);

        String status = response.getStatus();
        switch (status.toLowerCase()) {
            case "succeeded" -> {
                vo.setStatus("SUCCESS");
                // 从 content 里取视频 URL
                if (response.getContent() != null && response.getContent().getVideoUrl() != null) {
                    vo.setUrls(List.of(response.getContent().getVideoUrl()));
                }
                // 视频任务成功时按秒数计费，以 taskId 幂等，避免轮询重复计费
                recordVideoUsage(response, taskId);
            }
            case "failed" -> {
                vo.setStatus("FAILED");
                vo.setFailReason("视频生成失败");
            }
            default -> vo.setStatus("RUNNING");
        }

        return vo;
    }

    /**
     * 记录一次视频生成的秒数消耗
     * <p>以 taskId 幂等：轮询命中成功态多次也只记一条。model / duration 均取自方舟任务查询响应。
     */
    private void recordVideoUsage(GetContentGenerationTaskResponse response, String taskId) {
        try {
            Long duration = response.getDuration();
            if (duration == null) {
                log.debug("[火山引擎] 任务响应未返回 duration，跳过用量记录, taskId={}", taskId);
                return;
            }
            String model = response.getModel() != null ? response.getModel() : defaultVideoModel;
            usageRecordService.recordUsageOnce("volcano", model,
                AiUsageRecordService.USAGE_TYPE_VIDEO_SECONDS,
                BigDecimal.valueOf(duration), null, taskId);
        } catch (Exception e) {
            log.warn("[火山引擎] 记录视频用量失败，已忽略, taskId={}", taskId, e);
        }
    }

    /**
     * 把 (画幅比例, 分辨率档位) 归一化为方舟 size 字段。
     *
     * <p>方舟的 size 有两种写法：档位 shorthand（"1K"/"2K"）或显式像素（"2048x1152"）。
     * 档位 shorthand 不带画幅信息，实测传 "1K" 时方舟会自行决定比例（返回 832x1248），
     * 前端传的 aspectRatio 会被忽略；因此这里按画幅换算出显式像素。
     *
     * <p>长边基准：1k=1024，2k=2048；短边按 64 对齐。无法识别的档位或画幅回落到 "2K"，
     * 由方舟按默认画幅出图。
     */
    private String normalizeSize(String aspectRatio, String resolution) {
        // 档位不区分大小写，兼容前端传 "2k" 与默认的 "2K"；为空时按 2K 处理，避免丢掉画幅
        Map<String, Integer> longSideByResolution = Map.of("1K", 1024, "2K", 2048);
        String normalized = (resolution == null || resolution.isBlank() ? "2K" : resolution.trim()).toUpperCase();

        Integer longSide = longSideByResolution.get(normalized);
        // Map.of 不允许 null key，画幅可能为空，需先判空
        int[] ratio = aspectRatio == null ? null : AREA_BY_ASPECT.get(aspectRatio);
        if (longSide == null || ratio == null) {
            log.warn("[火山引擎] 未识别的画幅/分辨率组合(ratio={}, resolution={}), 回落到 2K",
                aspectRatio, resolution);
            return "2K";
        }

        int w = ratio[0];
        int h = ratio[1];
        // 短边按 64 对齐，避免方舟因非标准尺寸报错
        int shortSide = (int) (Math.round((double) longSide * Math.min(w, h) / Math.max(w, h) / 64) * 64);
        return w >= h ? longSide + "x" + shortSide : shortSide + "x" + longSide;
    }

}
