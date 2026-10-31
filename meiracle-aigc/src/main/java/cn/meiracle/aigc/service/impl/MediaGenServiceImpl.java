package cn.meiracle.aigc.service.impl;

import cn.meiracle.aigc.domain.bo.GenerateImageBo;
import cn.meiracle.aigc.domain.bo.GenerateVideoBo;
import cn.meiracle.aigc.domain.vo.GenerateResultVo;
import cn.meiracle.aigc.service.MediaGenService;
import cn.meiracle.aigc.service.strategy.MediaGenStrategy;
import cn.meiracle.aigc.service.strategy.MediaGenStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 媒体生成服务实现
 * 统一入口，通过策略工厂路由到不同厂商
 *
 * @author meiracle
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaGenServiceImpl implements MediaGenService {

    private final MediaGenStrategyFactory strategyFactory;

    @Override
    public GenerateResultVo generateImage(String provider, GenerateImageBo bo) {
        log.info("文生图请求, provider: {}, prompt: {}", provider, bo.getPrompt());
        MediaGenStrategy strategy = strategyFactory.getStrategy(provider);
        return strategy.generateImage(bo);
    }

    @Override
    public GenerateResultVo generateImageWithRef(String provider, GenerateImageBo bo) {
        log.info("参考图生图请求, provider: {}, ref: {}", provider, bo.getReferenceImageUrl());
        MediaGenStrategy strategy = strategyFactory.getStrategy(provider);
        return strategy.generateImageWithRef(bo);
    }

    @Override
    public GenerateResultVo generateVideo(String provider, GenerateVideoBo bo) {
        log.info("文生视频请求, provider: {}, duration: {}s", provider, bo.getDuration());
        MediaGenStrategy strategy = strategyFactory.getStrategy(provider);
        return strategy.generateVideo(bo);
    }

    @Override
    public GenerateResultVo generateVideoWithImage(String provider, GenerateVideoBo bo) {
        log.info("图生视频请求, provider: {}, firstFrame: {}", provider, bo.getFirstFrameUrl());
        MediaGenStrategy strategy = strategyFactory.getStrategy(provider);
        return strategy.generateVideoWithImage(bo);
    }

    @Override
    public GenerateResultVo queryTaskStatus(String provider, String taskId) {
        log.info("查询任务状态, provider: {}, taskId: {}", provider, taskId);
        MediaGenStrategy strategy = strategyFactory.getStrategy(provider);
        return strategy.queryTaskStatus(taskId);
    }

    @Override
    public List<String> listProviders() {
        return strategyFactory.listProviders();
    }

}
