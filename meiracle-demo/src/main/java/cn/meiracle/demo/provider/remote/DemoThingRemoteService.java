package cn.meiracle.demo.provider.remote;

import cn.meiracle.demo.biz.DemoThingBiz;
import cn.meiracle.demo.export.api.DemoThingApi;
import cn.meiracle.demo.export.model.DemoThingDTO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 远程服务实现层（provider/remote）：实现 export 契约 {@link DemoThingApi}，
 * 并作为 Dubbo 服务发布。消费方依赖 export 契约即可远程调用。
 */
@DubboService
@RequiredArgsConstructor
public class DemoThingRemoteService implements DemoThingApi {

    private final DemoThingBiz demoThingBiz;

    @Override
    public List<DemoThingDTO> listAll() {
        return demoThingBiz.listAll();
    }

    @Override
    public DemoThingDTO getById(Long id) {
        return demoThingBiz.getById(id);
    }

    @Override
    public Long create(String name, Integer status, String description) {
        return demoThingBiz.create(name, status, description);
    }

}
