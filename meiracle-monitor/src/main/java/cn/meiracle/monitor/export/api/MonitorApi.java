package cn.meiracle.monitor.export.api;

import cn.meiracle.monitor.export.model.ChangeRecordDTO;

import java.util.List;

/**
 * 监控/变动追踪远程服务契约（export 层）。
 * 由本服务在 {@code provider.remote} 中实现并发布为 Dubbo 服务。
 */
public interface MonitorApi {

    /**
     * 记录一条变动
     *
     * @return 主键
     */
    Long recordChange(String bizDomain, String bizKey, Integer changeType, String beforeSnapshot, String afterSnapshot);

    /**
     * 查询某业务域的变动记录
     */
    List<ChangeRecordDTO> listByDomain(String bizDomain);

}
