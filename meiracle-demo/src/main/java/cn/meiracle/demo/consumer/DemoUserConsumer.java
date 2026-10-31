package cn.meiracle.demo.consumer;

import cn.meiracle.demo.export.domain.model.DemoUserDTO;
import cn.meiracle.demo.export.service.DemoUserProvider;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * Dubbo consumer 示例（消费本模块自己发布的 provider）。
 * <p>
 * 通过 {@code @DubboReference} 注入 {@link DemoUserProvider} 的远程代理，
 * 经 Nacos 注册中心发现本应用在 {@code provider.remote} 中发布的同名服务。
 * 这是最简「consumer 消费自身 provider」参考写法：其它服务消费 demo 时，
 * 只需依赖 demo 的 export 契约，再以同样方式 {@code @DubboReference} 即可。
 * <p>
 * {@code check = false}：启动时不强制检查 provider 是否已就绪（自消费场景下
 * provider 与 consumer 同应用启动，避免初始化时序问题）。
 */
@Component
public class DemoUserConsumer {

    @DubboReference(check = false)
    private DemoUserProvider demoUserProvider;

    /**
     * RPC 调用：按用户 ID 查询
     */
    public DemoUserDTO getUserById(Long id) {
        return demoUserProvider.getUserById(id);
    }

    /**
     * RPC 调用：创建用户
     */
    public Long createUser(String name, String email) {
        return demoUserProvider.createUser(name, email);
    }

}
