package cn.meiracle.demo.export.service;

import cn.meiracle.demo.export.domain.model.DemoThingDTO;

import java.util.List;

/**
 * DemoThing 远程服务契约（export/service 层）。
 * <p>
 * 该接口与 {@link cn.meiracle.demo.export.domain.model.DemoThingDTO} 一同构成「对外契约」，
 * 由本服务在 {@code provider.remote} 包中实现，并发布为 Dubbo 服务；
 * 其它服务只需依赖本 export 契约即可调用，无需依赖实现。
 */
public interface DemoThingRemoteService {

    /**
     * 查询全部
     */
    List<DemoThingDTO> listAll();

    /**
     * 按 ID 查询
     */
    DemoThingDTO getById(Long id);

    /**
     * 创建并返回主键
     */
    Long create(String name, Integer status, String description);

}
