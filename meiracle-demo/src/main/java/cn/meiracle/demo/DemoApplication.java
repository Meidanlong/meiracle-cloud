package cn.meiracle.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Meiracle Demo 业务样板启动类。
 * <p>
 * 演示三种调用链路（以「简单用户」为例）：
 * 1) HTTP：provider/controller -> service -> dao；
 * 2) Dubbo provider：export/service 契约 + provider/remote 以 {@code @DubboService} 发布；
 * 3) Dubbo consumer：consumer 包中 {@code @DubboReference} 消费本模块自己发布的 provider。
 * 另演示 domain/dao/service/biz/task/common 分层与 DemoThing 变动上报（见 client/MonitorClient）。
 *
 * @author meiracle
 */
@SpringBootApplication
@MapperScan("cn.meiracle.demo.dao.mapper")
@EnableScheduling
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
