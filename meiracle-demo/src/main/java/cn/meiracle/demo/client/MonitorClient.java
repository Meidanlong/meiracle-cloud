package cn.meiracle.demo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 监控客户端：demo 在关键数据变动时，把变动事件上报给 meiracle-monitor 的 HTTP 端点。
 * <p>
 * 这是 demo 与 monitor 之间的真实连通链路之一（变动追踪方向）。
 * monitor 地址通过 {@code monitor.base-url} 占位符配置，部署时替换为真实地址；
 * 若 monitor 暂时不可用，仅记录告警日志，不影响主流程。
 */
@Slf4j
@Component
public class MonitorClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${monitor.base-url:http://127.0.0.1:9211}")
    private String monitorBaseUrl;

    /**
     * 上报一条变动事件（demo -> monitor）。
     *
     * @param bizDomain     业务域标识
     * @param bizKey        实体主键
     * @param changeType    变动类型：1 新增 / 2 更新 / 3 删除
     * @param beforeSnapshot 变更前快照（JSON）
     * @param afterSnapshot  变更后快照（JSON）
     */
    public void recordChange(String bizDomain, String bizKey, int changeType, String beforeSnapshot, String afterSnapshot) {
        try {
            String url = monitorBaseUrl + "/monitor/change-record";
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("bizDomain", bizDomain);
            body.add("bizKey", bizKey);
            body.add("changeType", changeType);
            body.add("beforeSnapshot", beforeSnapshot);
            body.add("afterSnapshot", afterSnapshot);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body,
                    new org.springframework.http.HttpHeaders() {{
                        setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    }});
            restTemplate.postForObject(url, entity, Long.class);
            log.info("[demo->monitor] reported change: {}/{} type={}", bizDomain, bizKey, changeType);
        } catch (Exception e) {
            log.warn("[demo->monitor] failed to report change (ignored): {}", e.getMessage());
        }
    }

}
