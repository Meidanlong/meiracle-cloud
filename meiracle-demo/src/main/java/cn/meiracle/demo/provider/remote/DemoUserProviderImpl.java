package cn.meiracle.demo.provider.remote;

import cn.meiracle.demo.export.domain.model.DemoUserDTO;
import cn.meiracle.demo.export.service.DemoUserProvider;
import cn.meiracle.demo.service.DemoUserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * DemoUser 远程服务实现层（provider/remote）：实现 export 契约
 * {@link DemoUserProvider}，并作为 Dubbo 服务发布。
 * <p>
 * 本模块的 consumer 示例（consumer/DemoUserConsumer）即消费此服务，
 * 其它服务也可依赖 export 契约后远程调用。
 */
@DubboService
@RequiredArgsConstructor
public class DemoUserProviderImpl implements DemoUserProvider {

    private final DemoUserService demoUserService;

    @Override
    public DemoUserDTO getUserById(Long id) {
        return demoUserService.getById(id);
    }

    @Override
    public Long createUser(String name, String email) {
        return demoUserService.create(name, email);
    }

}
