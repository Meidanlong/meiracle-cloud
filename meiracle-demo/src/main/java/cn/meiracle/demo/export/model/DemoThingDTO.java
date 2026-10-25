package cn.meiracle.demo.export.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DemoThing 对外 DTO（export 契约的一部分）。
 * 跨模块/跨服务调用时只传输该 DTO，不暴露内部 domain 实体。
 */
@Data
public class DemoThingDTO implements Serializable {

    private Long id;
    private String name;
    private Integer status;
    private String description;
    private LocalDateTime createTime;

}
