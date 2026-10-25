package cn.meiracle.file;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件中心启动类。
 *
 * @author meiracle-file
 */
@EnableDubbo
@SpringBootApplication
public class MeiracleFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeiracleFileApplication.class, args);
    }
}
