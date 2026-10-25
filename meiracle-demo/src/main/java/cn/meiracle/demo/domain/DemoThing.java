package cn.meiracle.demo.domain;

import cn.meiracle.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 示例业务实体：通用「事物(DemoThing)」表。
 * 仅用于模板演示，不含任何真实业务字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_thing")
public class DemoThing extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态：0 禁用 / 1 启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

}
