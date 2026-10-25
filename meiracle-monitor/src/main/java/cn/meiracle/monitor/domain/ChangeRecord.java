package cn.meiracle.monitor.domain;

import cn.meiracle.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 变动追踪记录：记录被监控实体发生的关键字段变更，用于审计与差异比对。
 * 通用模板实体，不含任何真实业务字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_change_record")
public class ChangeRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务域标识（如 user / order，由接入方自定义）
     */
    private String bizDomain;

    /**
     * 实体主键
     */
    private String bizKey;

    /**
     * 变更类型：1 新增 / 2 更新 / 3 删除
     */
    private Integer changeType;

    /**
     * 变更前快照（JSON）
     */
    private String beforeSnapshot;

    /**
     * 变更后快照（JSON）
     */
    private String afterSnapshot;

}
