package cn.meiracle.demo.service.impl;

import cn.meiracle.demo.consumer.DemoUserConsumer;
import cn.meiracle.demo.dao.mapper.DemoUserMapper;
import cn.meiracle.demo.domain.DemoUser;
import cn.meiracle.demo.export.domain.model.DemoUserDTO;
import cn.meiracle.demo.service.DemoUserService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * DemoUser 服务实现。
 * <p>
 * 同时演示两种读取链路：
 * - {@link #getById}：本地直查 dao；
 * - {@link #getByIdViaDubbo}：委托 consumer 走 Dubbo RPC，消费本模块自己发布的
 *   {@code DemoUserProvider}（RPC 到达 provider/remote 实现后再次回到本服务，形成完整链路闭环）。
 */
@Service
@RequiredArgsConstructor
public class DemoUserServiceImpl extends ServiceImpl<DemoUserMapper, DemoUser> implements DemoUserService {

    private final DemoUserConsumer demoUserConsumer;

    @Override
    public DemoUserDTO getById(Long id) {
        DemoUser user = getBaseMapper().selectByIdXml(id);
        return toDto(user);
    }

    @Override
    public DemoUserDTO getByIdViaDubbo(Long id) {
        return demoUserConsumer.getUserById(id);
    }

    @Override
    public Long create(String name, String email) {
        DemoUser user = new DemoUser();
        user.setName(name);
        user.setEmail(email);
        save(user);
        return user.getId();
    }

    private DemoUserDTO toDto(DemoUser user) {
        if (user == null) {
            return null;
        }
        DemoUserDTO dto = new DemoUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
