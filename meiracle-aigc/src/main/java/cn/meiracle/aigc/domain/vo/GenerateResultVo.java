package cn.meiracle.aigc.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 媒体生成结果
 *
 * @author meiracle
 */
@Data
public class GenerateResultVo {

    /**
     * 任务 ID（异步任务用，轮询时传这个）
     */
    private String taskId;

    /**
     * 任务状态：PENDING / RUNNING / SUCCESS / FAILED
     */
    private String status;

    /**
     * 生成的图片/视频 URL 列表
     */
    private List<String> urls;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 厂商原始返回（调试用，可隐藏）
     */
    private String rawResponse;

}
