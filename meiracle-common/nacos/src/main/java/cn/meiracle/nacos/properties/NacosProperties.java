package cn.meiracle.nacos.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nacos 连接配置（占位能力类）。
 * <p>
 * 业务模块可在自己的 application.yml 中配置 {@code meiracle.nacos.*} 来注入 Nacos 地址，
 * 实际注册/发现由 Spring Cloud Alibaba / Dubbo Registry 完成。
 *
 * @author meiracle
 */
@Data
@ConfigurationProperties(prefix = "meiracle.nacos")
public class NacosProperties {

    /**
     * Nacos 服务地址，例如 127.0.0.1:8848
     */
    private String serverAddr = "127.0.0.1:8848";

    /**
     * 命名空间（默认 public 为空）
     */
    private String namespace = "";

    /**
     * 分组
     */
    private String group = "DEFAULT_GROUP";

    /**
     * 用户名（生产环境请通过配置中心/环境变量注入，不要硬编码）
     */
    private String username = "nacos";

    /**
     * 密码（生产环境请通过配置中心/环境变量注入，不要硬编码）
     */
    private String password = "nacos";

}
