package cn.meiracle.monitor.provider.remote;

import cn.meiracle.monitor.biz.ChangeRecordBiz;
import cn.meiracle.monitor.export.api.MonitorApi;
import cn.meiracle.monitor.export.model.ChangeRecordDTO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 监控远程服务实现层（provider/remote）：实现 {@link MonitorApi} 并作为 Dubbo 服务发布。
 */
@DubboService
@RequiredArgsConstructor
public class MonitorRemoteService implements MonitorApi {

    private final ChangeRecordBiz changeRecordBiz;

    @Override
    public Long recordChange(String bizDomain, String bizKey, Integer changeType, String beforeSnapshot, String afterSnapshot) {
        return changeRecordBiz.recordChange(bizDomain, bizKey, changeType, beforeSnapshot, afterSnapshot);
    }

    @Override
    public List<ChangeRecordDTO> listByDomain(String bizDomain) {
        return changeRecordBiz.listByDomain(bizDomain);
    }

}
