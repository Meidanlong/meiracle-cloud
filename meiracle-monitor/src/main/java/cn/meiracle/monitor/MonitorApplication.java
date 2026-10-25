package cn.meiracle.monitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Meiracle Monitor 启动类：通用微服务监控能力（变动追踪 / 指标采集 / 告警基础框架）。
 *
 * @author meiracle
 */
@SpringBootApplication
@MapperScan("cn.meiracle.monitor.dao.mapper")
@EnableScheduling
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }

}
