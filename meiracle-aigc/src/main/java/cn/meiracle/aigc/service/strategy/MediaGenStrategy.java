package cn.meiracle.aigc.service.strategy;

import cn.meiracle.aigc.domain.bo.GenerateImageBo;
import cn.meiracle.aigc.domain.bo.GenerateVideoBo;
import cn.meiracle.aigc.domain.vo.GenerateResultVo;

/**
 * 媒体生成策略接口（策略模式核心）
 * 不同厂商（可灵/即梦/SD/Midjourney）实现此接口，统一对外暴露生图/生视频能力
 *
 * @author meiracle
 */
public interface MediaGenStrategy {

    /**
     * 获取策略标识（厂商名）
     */
    String getProvider();

    /**
     * 文生图
     */
    GenerateResultVo generateImage(GenerateImageBo bo);

    /**
     * 图生图（参考图）
     */
    GenerateResultVo generateImageWithRef(GenerateImageBo bo);

    /**
     * 文生视频
     */
    GenerateResultVo generateVideo(GenerateVideoBo bo);

    /**
     * 图生视频
     */
    GenerateResultVo generateVideoWithImage(GenerateVideoBo bo);

    /**
     * 查询任务状态（轮询用）
     */
    GenerateResultVo queryTaskStatus(String taskId);

}
