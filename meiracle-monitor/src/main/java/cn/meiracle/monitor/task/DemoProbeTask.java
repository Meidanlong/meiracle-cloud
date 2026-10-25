package cn.meiracle.monitor.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 探测任务（task 层）：周期性主动采集 demo 服务的健康状态与业务数据，
 * 是 monitor 与 demo 之间「健康/数据采集」方向的真实连通链路。
 * <p>
 * demo 地址通过 {@code demo.base-url} 占位符配置，部署时替换为真实地址；
 * 若 demo 暂不可用，仅记录告警日志，不影响 monitor 自身启动。
 */
@Slf4j
@Component
public class DemoProbeTask {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${demo.base-url:http://127.0.0.1:9201}")
    private String demoBaseUrl;

    /**
     * 每 30 秒探测一次 demo 的 actuator 健康与示例业务数据。
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void probe() {
        try {
            ResponseEntity<String> health = restTemplate.getForEntity(
                    demoBaseUrl + "/demo/actuator/health", String.class);
            log.info("[monitor->demo] health status = {}", health.getStatusCode());

            ResponseEntity<String> list = restTemplate.getForEntity(
                    demoBaseUrl + "/demo/demo-thing/list", String.class);
            log.info("[monitor->demo] demo_thing data sample = {}", list.getBody());
        } catch (Exception e) {
            log.warn("[monitor->demo] probe failed (demo may be down): {}", e.getMessage());
        }
    }

}
