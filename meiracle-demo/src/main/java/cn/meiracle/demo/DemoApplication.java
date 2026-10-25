package cn.meiracle.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Meiracle Demo 业务样板启动类。
 * 演示 domain/dao/service/biz/task/common/export/provider 分层与 Dubbo export 契约。
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
