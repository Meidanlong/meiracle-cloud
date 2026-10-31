package cn.meiracle.aigc.domain.bo;

import lombok.Data;

/**
 * 生图请求参数
 *
 * @author meiracle
 */
@Data
public class GenerateImageBo {

    /**
     * 提示词（正向描述）
     */
    private String prompt;

    /**
     * 负向提示词（不想要的内容）
     */
    private String negativePrompt;

    /**
     * 画幅比例，如 9:16 / 16:9 / 1:1
     */
    private String aspectRatio = "9:16";

    /**
     * 分辨率档位，1k / 2k
     * 方舟要求大写（1K / 2K），此处默认大写；小写入参由 VolcanoArkStrategy 归一化
     */
    private String resolution = "2K";

    /**
     * 生成数量
     */
    private Integer n = 1;

    /**
     * 参考图 URL（图生图时用）
     */
    private String referenceImageUrl;

    /**
     * 模型名称（可选，默认用厂商默认模型）
     */
    private String model;

}
