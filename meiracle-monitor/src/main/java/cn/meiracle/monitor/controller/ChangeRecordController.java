package cn.meiracle.monitor.controller;

import cn.meiracle.monitor.biz.ChangeRecordBiz;
import cn.meiracle.monitor.export.domain.model.ChangeRecordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 变动追踪 HTTP 接入层。
 */
@RestController
@RequestMapping("/change-record")
@RequiredArgsConstructor
public class ChangeRecordController {

    private final ChangeRecordBiz changeRecordBiz;

    @PostMapping
    public Long record(@RequestParam String bizDomain,
                       @RequestParam String bizKey,
                       @RequestParam Integer changeType,
                       @RequestParam(required = false) String beforeSnapshot,
                       @RequestParam(required = false) String afterSnapshot) {
        return changeRecordBiz.recordChange(bizDomain, bizKey, changeType, beforeSnapshot, afterSnapshot);
    }

    @GetMapping
    public List<ChangeRecordDTO> list(@RequestParam String bizDomain) {
        return changeRecordBiz.listByDomain(bizDomain);
    }

}
