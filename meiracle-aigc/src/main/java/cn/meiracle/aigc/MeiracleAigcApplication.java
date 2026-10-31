package cn.meiracle.aigc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 模块启动类（meiracle-aigc）。
 *
 * <p>包扫描同时覆盖 {@code cn.meiracle.aigc}（本模块）与 {@code cn.meiracle.http}
 * （meiracle-common 的 http 能力，自动装配 {@code HttpTemplate} 供媒体生成策略调用三方平台）。
 * 不扫描、不暴露 Dubbo 契约——本模块对外仅提供 HTTP 接口（供其他业务经 HTTP 调用）。</p>
 *
 * @author meiracle
 */
@SpringBootApplication(scanBasePackages = {"cn.meiracle.aigc", "cn.meiracle.http"})
@MapperScan("cn.meiracle.aigc.dao.mapper")
public class MeiracleAigcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeiracleAigcApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  meiracle-aigc 启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
