package cn.meiracle.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 通用网关启动程序
 *
 * <p>职责：入口登录校验（Sa-Token）+ 路由转发 + 白名单。不做业务、不管权限点。</p>
 *
 * @author meiracle
 */
@SpringBootApplication
public class MeiracleGatewayApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MeiracleGatewayApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  meiracle-gateway 启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

}
