package cn.meiracle.http.config;

import cn.meiracle.http.client.HttpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * meiracle-common http 能力自动装配。
 *
 * <p>暴露 {@link HttpTemplate} 为 Spring Bean，业务模块在 {@code scanBasePackages} 加入
 * {@code cn.meiracle.http} 即可直接注入使用；超时可通过配置项覆盖：</p>
 * <ul>
 *     <li>{@code meiracle.http.connect-timeout}：连接超时（毫秒，默认 10000）</li>
 *     <li>{@code meiracle.http.read-timeout}：读取超时（毫秒，默认 60000）</li>
 * </ul>
 *
 * @author meiracle
 */
@Configuration
public class HttpConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HttpTemplate httpTemplate(
            @Value("${meiracle.http.connect-timeout:10000}") int connectTimeout,
            @Value("${meiracle.http.read-timeout:60000}") int readTimeout) {
        return new HttpTemplate(connectTimeout, readTimeout);
    }
}
