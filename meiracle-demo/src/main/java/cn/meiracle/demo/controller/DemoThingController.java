package cn.meiracle.demo.controller;

import cn.meiracle.demo.biz.DemoThingBiz;
import cn.meiracle.demo.export.model.DemoThingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HTTP 接入层：仅做参数校验与协议转换，业务委托给 biz。
 */
@RestController
@RequestMapping("/demo-thing")
@RequiredArgsConstructor
public class DemoThingController {

    private final DemoThingBiz demoThingBiz;

    @GetMapping("/list")
    public List<DemoThingDTO> list() {
        return demoThingBiz.listAll();
    }

    @GetMapping("/{id}")
    public DemoThingDTO get(@PathVariable Long id) {
        return demoThingBiz.getById(id);
    }

    @PostMapping
    public Long create(@RequestParam String name,
                       @RequestParam Integer status,
                       @RequestParam(required = false) String description) {
        return demoThingBiz.create(name, status, description);
    }

    @PutMapping("/{id}/status")
    public boolean updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return demoThingBiz.updateStatus(id, status);
    }

}
