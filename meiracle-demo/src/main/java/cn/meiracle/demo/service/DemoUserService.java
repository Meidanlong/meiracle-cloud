package cn.meiracle.demo.service;

import cn.meiracle.demo.export.domain.model.DemoUserDTO;

/**
 * DemoUser 服务层接口。
 * <p>
 * 演示同一数据的两种读取链路：
 * 1) {@link #getById}：本地直查（controller -> service -> dao）；
 * 2) {@link #getByIdViaDubbo}：经 Dubbo consumer 回调自身 provider 的 RPC 链路
 * （controller -> service -> consumer -> @DubboReference -> provider -> service -> dao）。
 */
public interface DemoUserService {

    /**
     * 按 ID 本地直查（未命中返回 null）
     */
    DemoUserDTO getById(Long id);

    /**
     * 按 ID 走 Dubbo RPC 链路查询（消费本模块自己发布的 provider）
     */
    DemoUserDTO getByIdViaDubbo(Long id);

    /**
     * 创建用户并返回主键
     */
    Long create(String name, String email);

}
