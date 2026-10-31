package cn.meiracle.demo.provider.controller;

import cn.meiracle.demo.export.domain.model.DemoUserDTO;
import cn.meiracle.demo.service.DemoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DemoUser HTTP 接入层（provider/controller）。
 * <p>
 * 以「简单用户」演示三种链路中的 HTTP 入口：
 * - GET /user/{id}        ：直查链路（controller -> service -> dao）；
 * - GET /user/dubbo/{id}  ：Dubbo 链路（controller -> service -> consumer -> provider -> dao）；
 * - POST /user            ：本地写入（service -> dao）。
 * 上下文路径为 /demo，完整路径即 /demo/user/**。
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class DemoUserController {

    private final DemoUserService demoUserService;

    /**
     * 链路一：本地直查
     */
    @GetMapping("/{id}")
    public DemoUserDTO get(@PathVariable Long id) {
        return demoUserService.getById(id);
    }

    /**
     * 链路二：经 Dubbo consumer 消费自身 provider 的 RPC 链路
     */
    @GetMapping("/dubbo/{id}")
    public DemoUserDTO getByDubbo(@PathVariable Long id) {
        return demoUserService.getByIdViaDubbo(id);
    }

    /**
     * 创建用户（返回主键）
     */
    @PostMapping
    public Long create(@RequestParam String name,
                       @RequestParam(required = false) String email) {
        return demoUserService.create(name, email);
    }

}
