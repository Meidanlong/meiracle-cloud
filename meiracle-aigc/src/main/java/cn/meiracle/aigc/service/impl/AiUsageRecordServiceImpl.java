package cn.meiracle.aigc.service.impl;

import cn.meiracle.aigc.dao.mapper.AiUsageRecordMapper;
import cn.meiracle.aigc.domain.entity.AiUsageRecord;
import cn.meiracle.aigc.service.AiUsageRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * AI 用量记录 服务层处理
 *
 * @author meiracle
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiUsageRecordServiceImpl implements AiUsageRecordService {

    private final AiUsageRecordMapper usageRecordMapper;

    @Override
    public void recordUsage(String provider, String model, String usageType,
                            BigDecimal usageAmount, BigDecimal costAmount, String taskId) {
        // 用量统计属于旁路能力，失败不应影响生图/生视频主流程，因此整体兜底
        try {
            AiUsageRecord record = new AiUsageRecord();
            record.setUserId(currentUserId());
            record.setModule(MODULE_MEDIA);
            record.setProvider(provider);
            record.setModel(model);
            record.setUsageType(usageType);
            record.setUsageAmount(usageAmount);
            record.setCostAmount(costAmount);
            record.setTaskId(taskId);
            usageRecordMapper.insert(record);
            log.info("[用量记录] provider={}, model={}, type={}, amount={}, taskId={}",
                provider, model, usageType, usageAmount, taskId);
        } catch (Exception e) {
            log.warn("[用量记录] 记录失败，已忽略, provider={}, model={}, taskId={}",
                provider, model, taskId, e);
        }
    }

    @Override
    public boolean recordUsageOnce(String provider, String model, String usageType,
                                   BigDecimal usageAmount, BigDecimal costAmount, String taskId) {
        // 无 taskId 无法做幂等，退化为普通记录
        if (taskId == null || taskId.isBlank()) {
            recordUsage(provider, model, usageType, usageAmount, costAmount, taskId);
            return true;
        }
        try {
            boolean exists = usageRecordMapper.exists(
                Wrappers.<AiUsageRecord>lambdaQuery()
                    .eq(AiUsageRecord::getTaskId, taskId)
                    .eq(AiUsageRecord::getUsageType, usageType));
            if (exists) {
                log.debug("[用量记录] taskId={} type={} 已记录，跳过", taskId, usageType);
                return false;
            }
            recordUsage(provider, model, usageType, usageAmount, costAmount, taskId);
            return true;
        } catch (Exception e) {
            log.warn("[用量记录] 幂等记录失败，已忽略, taskId={}, type={}", taskId, usageType, e);
            return false;
        }
    }

    /**
     * 获取当前登录用户ID；本模块当前未接入鉴权上下文（对外 HTTP 接口无 Sa-Token 会话），
     * 内部异步轮询也无登录态，故统一返回 null。后续若接入统一登录，在此处改为从登录态取值即可。
     */
    private Long currentUserId() {
        return null;
    }

}
