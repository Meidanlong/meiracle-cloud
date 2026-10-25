package cn.meiracle.demo.biz;

import cn.meiracle.demo.client.MonitorClient;
import cn.meiracle.demo.domain.DemoThing;
import cn.meiracle.demo.export.domain.model.DemoThingDTO;
import cn.meiracle.demo.service.DemoThingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务编排层（biz）：组合多个 service / 跨域逻辑 / 事务边界。
 * controller 与 provider 都调用 biz，避免在 controller 里写业务。
 */
@Component
@RequiredArgsConstructor
public class DemoThingBiz {

    private final DemoThingService demoThingService;
    private final MonitorClient monitorClient;

    /**
     * 创建事物（事务边界在 biz 层），并在提交后向 monitor 上报变动事件。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(String name, Integer status, String description) {
        DemoThing thing = new DemoThing();
        thing.setName(name);
        thing.setStatus(status);
        thing.setDescription(description);
        demoThingService.save(thing);
        Long id = thing.getId();
        monitorClient.recordChange("demo_thing", String.valueOf(id), 1, null, toSnapshot(thing));
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        DemoThing before = demoThingService.getById(id);
        if (before == null) {
            return false;
        }
        before.setStatus(status);
        boolean ok = demoThingService.updateById(before);
        monitorClient.recordChange("demo_thing", String.valueOf(id), 2,
                toSnapshot(before), toSnapshot(before));
        return ok;
    }

    public List<DemoThingDTO> listAll() {
        return demoThingService.list().stream().map(this::toDto).collect(Collectors.toList());
    }

    public DemoThingDTO getById(Long id) {
        return toDto(demoThingService.getById(id));
    }

    private String toSnapshot(DemoThing t) {
        if (t == null) {
            return null;
        }
        return "{\"name\":\"" + t.getName() + "\",\"status\":" + t.getStatus() + "}";
    }

    private DemoThingDTO toDto(DemoThing t) {
        if (t == null) {
            return null;
        }
        DemoThingDTO dto = new DemoThingDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setStatus(t.getStatus());
        dto.setDescription(t.getDescription());
        dto.setCreateTime(t.getCreateTime());
        return dto;
    }

}
