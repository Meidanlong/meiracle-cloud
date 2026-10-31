package cn.meiracle.aigc.service;

import cn.meiracle.aigc.domain.bo.GenerateImageBo;
import cn.meiracle.aigc.domain.bo.GenerateVideoBo;
import cn.meiracle.aigc.domain.vo.GenerateResultVo;

import java.util.List;

/**
 * 媒体生成服务接口
 *
 * @author meiracle
 */
public interface MediaGenService {

    /**
     * 文生图
     *
     * @param provider 厂商：kling / volcano
     * @param bo       生图参数
     */
    GenerateResultVo generateImage(String provider, GenerateImageBo bo);

    /**
     * 参考图生图
     */
    GenerateResultVo generateImageWithRef(String provider, GenerateImageBo bo);

    /**
     * 文生视频
     */
    GenerateResultVo generateVideo(String provider, GenerateVideoBo bo);

    /**
     * 图生视频
     */
    GenerateResultVo generateVideoWithImage(String provider, GenerateVideoBo bo);

    /**
     * 查询任务状态
     */
    GenerateResultVo queryTaskStatus(String provider, String taskId);

    /**
     * 获取所有支持的厂商列表
     */
    List<String> listProviders();

}
