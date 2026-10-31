package cn.meiracle.aigc.provider.controller;

import cn.meiracle.aigc.domain.bo.GenerateImageBo;
import cn.meiracle.aigc.domain.bo.GenerateVideoBo;
import cn.meiracle.aigc.domain.vo.GenerateResultVo;
import cn.meiracle.aigc.service.MediaGenService;
import cn.meiracle.core.domain.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 媒体生成 Controller（对外 HTTP 接口，供其他业务经 HTTP 调用，不走 Dubbo）
 * 生图/生视频统一入口，通过 provider 参数切换厂商
 *
 * @author meiracle
 */
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaGenController {

    private final MediaGenService mediaGenService;

    /**
     * 获取所有支持的厂商列表
     */
    @GetMapping("/providers")
    public R<List<String>> listProviders() {
        return R.ok(mediaGenService.listProviders());
    }

    /**
     * 文生图
     *
     * @param provider 厂商：kling / volcano
     */
    @PostMapping("/image/text-to-image")
    public R<GenerateResultVo> generateImage(@RequestParam String provider,
                                             @RequestBody GenerateImageBo bo) {
        return R.ok(mediaGenService.generateImage(provider, bo));
    }

    /**
     * 参考图生图
     */
    @PostMapping("/image/image-to-image")
    public R<GenerateResultVo> generateImageWithRef(@RequestParam String provider,
                                                    @RequestBody GenerateImageBo bo) {
        return R.ok(mediaGenService.generateImageWithRef(provider, bo));
    }

    /**
     * 文生视频
     */
    @PostMapping("/video/text-to-video")
    public R<GenerateResultVo> generateVideo(@RequestParam String provider,
                                            @RequestBody GenerateVideoBo bo) {
        return R.ok(mediaGenService.generateVideo(provider, bo));
    }

    /**
     * 图生视频
     */
    @PostMapping("/video/image-to-video")
    public R<GenerateResultVo> generateVideoWithImage(@RequestParam String provider,
                                                      @RequestBody GenerateVideoBo bo) {
        return R.ok(mediaGenService.generateVideoWithImage(provider, bo));
    }

    /**
     * 查询异步任务状态
     */
    @GetMapping("/task/{provider}/{taskId}")
    public R<GenerateResultVo> queryTaskStatus(@PathVariable String provider,
                                               @PathVariable String taskId) {
        return R.ok(mediaGenService.queryTaskStatus(provider, taskId));
    }

}
