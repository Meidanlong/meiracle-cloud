package cn.meiracle.demo.export.domain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * DemoUser 对外 DTO（export 契约的一部分）。
 * Dubbo 跨服务调用时只传输该 DTO，不暴露内部 domain 实体。
 */
@Data
public class DemoUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String email;

}
