package cn.meiracle.monitor.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 指标采集/健康检查任务（task 层示例）：周期性采集本服务指标，可对接 Prometheus / 告警通道。
 */
@Slf4j
@Component
public class MetricsCollectTask {

    /**
     * 每 30 秒采集一次（仅作模板演示，真实实现可上报 QPS / 延迟 / 资源水位）。
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void collect() {
        try {
            log.info("[monitor-task] metrics collected at {}", System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("[monitor-task] collect failed: {}", e.getMessage());
        }
    }

}
