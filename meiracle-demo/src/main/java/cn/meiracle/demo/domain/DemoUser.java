package cn.meiracle.demo.domain;

import cn.meiracle.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 示例「简单用户」实体：仅 id / name / email 三个演示字段，
 * 不含任何真实业务。用于演示 HTTP / Dubbo provider / Dubbo consumer 三种链路。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_user")
public class DemoUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

}
