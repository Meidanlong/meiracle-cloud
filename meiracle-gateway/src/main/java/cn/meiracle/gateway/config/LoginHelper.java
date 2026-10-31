package cn.meiracle.gateway.config;

/**
 * 登录态辅助常量（本地定义，替代原 org.dromara.common.satoken.utils.LoginHelper）。
 *
 * <p>这些常量用于从 Sa-Token Token-Session 中读取「客户端维度」的扩展信息，
 * 由业务 auth 服务在登录时写入（{@code StpUtil.getTokenSession().set(KEY, value)}）。
 * 网关仅做校验，不负责写入；若对应 extra 不存在则对应规则自动放行（向后兼容）。</p>
 *
 * <p>注意：常量取值需与业务 auth 服务保持一致。</p>
 *
 * @author meiracle
 */
public final class LoginHelper {

    private LoginHelper() {
    }

    /**
     * 登录用户对象在 Token-Session 中的 key
     */
    public static final String LOGIN_USER_KEY = "login_user";

    /**
     * 客户端ID（请求头 / 参数名也为 clientid）
     */
    public static final String CLIENT_KEY = "clientid";

    /**
     * 客户端允许访问的接口路径（逗号 / 分号 / 换行分隔）
     */
    public static final String CLIENT_ACCESS_PATH_KEY = "client_access_path";

    /**
     * 客户端允许的来源 IP 白名单（逗号 / 分号 / 换行分隔）
     */
    public static final String CLIENT_IP_WHITELIST_KEY = "client_ip_whitelist";

}
