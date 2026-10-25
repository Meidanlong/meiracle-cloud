package cn.meiracle.demo.common.constants;

/**
 * 通用常量（common 层示例）。业务模块可在此放置状态码、缓存 key 前缀等共享常量。
 */
public interface DemoConstants {

    /**
     * 示例事物的缓存 key 前缀
     */
    String DEMO_THING_KEY = "demo:thing:";

    /**
     * 状态：启用
     */
    int STATUS_ENABLED = 1;

    /**
     * 状态：禁用
     */
    int STATUS_DISABLED = 0;

}
