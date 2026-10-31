package cn.meiracle.demo.export.service;

import cn.meiracle.demo.export.domain.model.DemoUserDTO;

/**
 * DemoUser 远程服务契约（export/service 层）。
 * <p>
 * 以「简单用户」演示 Dubbo provider 写法：接口定义在 export/service，
 * 由 {@code provider.remote} 包中的实现类以 {@code @DubboService} 发布；
 * 消费方（含本模块自己的 consumer 示例）只需依赖 export 契约即可调用。
 */
public interface DemoUserProvider {

    /**
     * 按用户 ID 查询（未命中返回 null）
     */
    DemoUserDTO getUserById(Long id);

    /**
     * 创建用户并返回主键
     */
    Long createUser(String name, String email);

}
