package cn.meiracle.monitor.export.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 变动追踪对外 DTO（export 契约）。
 */
@Data
public class ChangeRecordDTO implements Serializable {

    private Long id;
    private String bizDomain;
    private String bizKey;
    private Integer changeType;
    private String beforeSnapshot;
    private String afterSnapshot;
    private LocalDateTime createTime;

}
