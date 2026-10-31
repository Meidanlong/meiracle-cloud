package cn.meiracle.http.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

/**
 * 通用 HTTP 调用模板（meiracle-common http 能力）。
 *
 * <p>基于 Spring 6 {@link RestClient} 封装，统一处理：GET / POST(JSON)、请求头、
 * 连接/读取超时、JSON 序列化与反序列化。业务模块（如 meiracle-aigc）调用三方 AI 平台
 * 时统一走本工具，避免在各自模块里 new RestTemplate / HttpClient，做到 HTTP 调用收敛。</p>
 *
 * <p>非 2xx 响应会抛出 {@link org.springframework.web.client.RestClientResponseException}，
 * 由调用方按需捕获处理（如媒体生成策略里统一兜成 FAILED）。</p>
 *
 * @author meiracle
 */
public class HttpTemplate {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    /**
     * 默认构造：连接 10s、读取 60s，内置默认 ObjectMapper。
     */
    public HttpTemplate() {
        this(10_000, 60_000, new ObjectMapper());
    }

    /**
     * 指定超时（毫秒）的构造。
     */
    public HttpTemplate(int connectTimeoutMs, int readTimeoutMs) {
        this(connectTimeoutMs, readTimeoutMs, new ObjectMapper());
    }

    /**
     * 全参构造，可注入外部 ObjectMapper（如 Spring 托管的格式化器）。
     */
    public HttpTemplate(int connectTimeoutMs, int readTimeoutMs, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    /**
     * GET 请求，返回响应体字符串。
     *
     * @param url     完整 URL
     * @param headers 自定义请求头（可为 null）
     */
    public String getForString(String url, Map<String, String> headers) {
        return restClient.get()
                .uri(url)
                .headers(h -> copyHeaders(headers, h))
                .retrieve()
                .body(String.class);
    }

    /**
     * POST JSON 请求，返回响应体字符串。
     *
     * @param url     完整 URL
     * @param headers 自定义请求头（可为 null），Content-Type 会被强制为 application/json
     * @param body    请求体对象（自动序列化为 JSON；为 null 时发送空体）
     */
    public String postJsonForString(String url, Map<String, String> headers, Object body) {
        String json = serialize(body);
        return restClient.post()
                .uri(url)
                .headers(h -> copyHeaders(headers, h))
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .body(String.class);
    }

    /**
     * GET 请求并反序列化为指定类型。
     */
    public <T> T getForObject(String url, Map<String, String> headers, Class<T> type) {
        return deserialize(getForString(url, headers), type);
    }

    /**
     * POST JSON 请求并反序列化为指定类型。
     */
    public <T> T postJsonForObject(String url, Map<String, String> headers, Object body, Class<T> type) {
        return deserialize(postJsonForString(url, headers, body), type);
    }

    private void copyHeaders(Map<String, String> source, HttpHeaders target) {
        if (source == null) {
            return;
        }
        source.forEach(target::set);
    }

    private String serialize(Object body) {
        if (body == null) {
            return "";
        }
        if (body instanceof String s) {
            return s;
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("HttpTemplate 请求体 JSON 序列化失败: " + e.getMessage(), e);
        }
    }

    private <T> T deserialize(String json, Class<T> type) {
        if (json == null || json.isBlank() || type == String.class) {
            return type.cast(json);
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("HttpTemplate 响应体 JSON 反序列化失败: " + e.getMessage(), e);
        }
    }
}
