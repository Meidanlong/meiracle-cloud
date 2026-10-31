package cn.meiracle.aigc.service;

import java.math.BigDecimal;

/**
 * AI 用量记录 服务层
 * media（媒体生成）/ aigc 等模块共用，通过 module 字段区分来源模块
 *
 * @author meiracle
 */
public interface AiUsageRecordService {

    /**
     * 模块标识：本模块写入 media
     */
    String MODULE_MEDIA = "media";

    /**
     * 消耗类型：token 数
     */
    String USAGE_TYPE_TOKEN = "token";

    /**
     * 消耗类型：图片张数
     */
    String USAGE_TYPE_IMAGE_COUNT = "image_count";

    /**
     * 消耗类型：视频秒数
     */
    String USAGE_TYPE_VIDEO_SECONDS = "video_seconds";

    /**
     * 记录一次用量消耗
     * <p>userId 取当前登录用户（无登录上下文时为 null）；记录失败不影响主业务流程，由实现方兜底吞掉异常。
     *
     * @param provider     厂商：kling / volcano / openai 等
     * @param model        具体模型名
     * @param usageType    消耗类型，见本接口常量
     * @param usageAmount  消耗数量
     * @param costAmount   折算费用（元），可为 null 表示暂不折算
     * @param taskId       关联任务ID，可为 null（同步接口无任务ID）
     */
    void recordUsage(String provider, String model, String usageType,
                     BigDecimal usageAmount, BigDecimal costAmount, String taskId);

    /**
     * 幂等记录一次用量消耗（异步任务专用）
     * <p>异步任务靠轮询推进，任务成功时可能被轮询到多次，因此以 taskId 作为幂等键：
     * 已存在同 taskId + usageType 的记录则直接跳过，不重复计费。
     *
     * @return true 表示本次真正写入了记录；false 表示已记录过而跳过，或写入失败
     */
    boolean recordUsageOnce(String provider, String model, String usageType,
                            BigDecimal usageAmount, BigDecimal costAmount, String taskId);

}
