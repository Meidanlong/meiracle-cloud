package cn.meiracle.aigc.domain.entity;

import cn.meiracle.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * AI 用量记录对象 ai_usage_record
 * media（媒体生成）/ aigc 等模块共用，通过 module 字段区分来源模块
 *
 * @author meiracle
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_usage_record")
public class AiUsageRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 使用该能力的用户ID（未登录的内部任务记为 null）
     */
    private Long userId;

    /**
     * 所属模块：media / aigc
     */
    private String module;

    /**
     * 厂商：kling / volcano / openai 等
     */
    private String provider;

    /**
     * 具体模型名
     */
    private String model;

    /**
     * 消耗类型：token / image_count / video_seconds
     */
    private String usageType;

    /**
     * 消耗数量（token数 / 张数 / 秒数）
     */
    private BigDecimal usageAmount;

    /**
     * 折算费用（元）
     */
    private BigDecimal costAmount;

    /**
     * 关联任务ID（厂商返回）
     */
    private String taskId;

}
