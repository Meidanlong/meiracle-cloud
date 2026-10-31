package cn.meiracle.aigc.domain.bo;

import lombok.Data;

/**
 * 生视频请求参数
 *
 * @author meiracle
 */
@Data
public class GenerateVideoBo {

    /**
     * 提示词（画面描述）
     */
    private String prompt;

    /**
     * 负向提示词
     */
    private String negativePrompt;

    /**
     * 首帧图 URL（图生视频时用）
     */
    private String firstFrameUrl;

    /**
     * 尾帧图 URL（首尾帧模式）
     */
    private String lastFrameUrl;

    /**
     * 画幅比例
     */
    private String aspectRatio = "9:16";

    /**
     * 时长（秒）
     */
    private Integer duration = 5;

    /**
     * 分辨率，720p / 1080p
     */
    private String resolution = "720p";

    /**
     * 是否生成音频
     */
    private Boolean generateAudio = false;

    /**
     * 模型名称（可选）
     */
    private String model;

}
