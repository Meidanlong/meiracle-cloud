package cn.meiracle.aigc.service.strategy;

import cn.meiracle.aigc.common.exception.MediaException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 媒体生成策略工厂
 * 自动收集所有 MediaGenStrategy 实现，根据 provider 名路由到对应策略
 *
 * @author meiracle
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MediaGenStrategyFactory {

    private final List<MediaGenStrategy> strategyList;

    private final Map<String, MediaGenStrategy> strategyMap = new HashMap<>();

    /**
     * 启动时把所有策略实现注册到 Map 里
     */
    @PostConstruct
    public void init() {
        for (MediaGenStrategy strategy : strategyList) {
            strategyMap.put(strategy.getProvider(), strategy);
            log.info("[媒体生成] 注册策略: {} -> {}", strategy.getProvider(), strategy.getClass().getSimpleName());
        }
        log.info("[媒体生成] 共加载 {} 个厂商策略", strategyMap.size());
    }

    /**
     * 根据 provider 获取对应策略实现
     */
    public MediaGenStrategy getStrategy(String provider) {
        MediaGenStrategy strategy = strategyMap.get(provider);
        if (strategy == null) {
            throw new MediaException("不支持的媒体生成厂商: " + provider + "，当前支持: " + strategyMap.keySet());
        }
        return strategy;
    }

    /**
     * 获取所有支持的厂商列表
     */
    public List<String> listProviders() {
        return List.copyOf(strategyMap.keySet());
    }

}
