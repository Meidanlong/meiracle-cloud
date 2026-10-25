package cn.meiracle.core.utils.ip;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.meiracle.core.utils.StringUtils;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;

import java.io.InputStream;

/**
 * IP地址行政区域工具类
 * 参考地址：<a href="https://gitee.com/lionsoul/ip2region/tree/master/binding/java">ip2region xdb java 查询客户端实现</a>
 * xdb数据库文件下载：<a href="https://gitee.com/lionsoul/ip2region/tree/master/data">ip2region data</a>
 *
 * @author 秋辞未寒
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionUtils {

    // 默认IPv4地址库文件路径
    public static final String DEFAULT_IPV4_XDB_PATH = "ip2region_v4.xdb";

    // 默认IPv6地址库文件路径
    public static final String DEFAULT_IPV6_XDB_PATH = "ip2region_v6.xdb";

    // 未知地址
    public static final String UNKNOWN_ADDRESS = "未知";

    private static volatile DbSearcher searcher;

    // 初始化Ip2Region服务实例
    static {
        try {
            InputStream is = ResourceUtil.getStream(DEFAULT_IPV4_XDB_PATH);
            if (is == null) {
                log.warn("未加载 IPv4 地址库：未在类路径下找到文件 {}，IP 归属地查询将返回未知。", DEFAULT_IPV4_XDB_PATH);
            } else {
                byte[] dbBytes = IoUtil.readBytes(is);
                searcher = new DbSearcher(new DbConfig(), dbBytes);
                log.debug("IP工具初始化成功，加载IP地址库数据成功！");
            }
        } catch (Exception e) {
            log.warn("RegionUtils初始化失败（若未配置 ip2region xdb 文件可忽略）：{}", e.getMessage());
            searcher = null;
        }
    }

    /**
     * 根据IP地址离线获取城市
     *
     * @param ipString ip地址字符串
     */
    public static String getRegion(String ipString) {
        if (searcher == null) {
            return UNKNOWN_ADDRESS;
        }
        try {
            String region = searcher.memorySearch(ipString).getRegion();
            if (StringUtils.isBlank(region)) {
                return UNKNOWN_ADDRESS;
            }
            return StringUtils.replace(region, "0", UNKNOWN_ADDRESS);
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ipString);
            return UNKNOWN_ADDRESS;
        }
    }

    /**
     * 根据IP地址离线获取城市
     *
     * @param ipBytes ip地址字节数组
     */
    public static String getRegion(byte[] ipBytes) {
        return getRegion(ipBytesToIp(ipBytes));
    }

    /**
     * 关闭Ip2Region服务
     */
    public static void close() {
        if (searcher == null) {
            return;
        }
        try {
            searcher.close();
        } catch (Exception e) {
            log.error("Ip2Region服务关闭异常", e);
        }
    }

    private static String ipBytesToIp(byte[] ipBytes) {
        if (ipBytes == null || ipBytes.length != 4) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : ipBytes) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(b & 0xFF);
        }
        return sb.toString();
    }

}
