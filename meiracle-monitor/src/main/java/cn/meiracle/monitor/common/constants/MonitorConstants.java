package cn.meiracle.monitor.common.constants;

/**
 * 监控通用常量（common 层示例）。
 */
public interface MonitorConstants {

    /**
     * 变动类型：新增
     */
    int CHANGE_CREATE = 1;

    /**
     * 变动类型：更新
     */
    int CHANGE_UPDATE = 2;

    /**
     * 变动类型：删除
     */
    int CHANGE_DELETE = 3;

    /**
     * 变动记录缓存 key 前缀
     */
    String CHANGE_RECORD_KEY = "monitor:change:";

}
