package cn.meiracle.monitor.biz;

import cn.meiracle.monitor.domain.ChangeRecord;
import cn.meiracle.monitor.export.domain.model.ChangeRecordDTO;
import cn.meiracle.monitor.service.ChangeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 变动追踪业务编排层。
 */
@Component
@RequiredArgsConstructor
public class ChangeRecordBiz {

    private final ChangeRecordService changeRecordService;

    @Transactional(rollbackFor = Exception.class)
    public Long recordChange(String bizDomain, String bizKey, Integer changeType, String beforeSnapshot, String afterSnapshot) {
        ChangeRecord record = new ChangeRecord();
        record.setBizDomain(bizDomain);
        record.setBizKey(bizKey);
        record.setChangeType(changeType);
        record.setBeforeSnapshot(beforeSnapshot);
        record.setAfterSnapshot(afterSnapshot);
        changeRecordService.save(record);
        return record.getId();
    }

    public List<ChangeRecordDTO> listByDomain(String bizDomain) {
        return changeRecordService.lambdaQuery()
                .eq(ChangeRecord::getBizDomain, bizDomain)
                .list().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ChangeRecordDTO toDto(ChangeRecord r) {
        if (r == null) {
            return null;
        }
        ChangeRecordDTO dto = new ChangeRecordDTO();
        dto.setId(r.getId());
        dto.setBizDomain(r.getBizDomain());
        dto.setBizKey(r.getBizKey());
        dto.setChangeType(r.getChangeType());
        dto.setBeforeSnapshot(r.getBeforeSnapshot());
        dto.setAfterSnapshot(r.getAfterSnapshot());
        dto.setCreateTime(r.getCreateTime());
        return dto;
    }

}
