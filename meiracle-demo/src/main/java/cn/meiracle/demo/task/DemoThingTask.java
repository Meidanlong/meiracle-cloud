package cn.meiracle.demo.task;

import cn.meiracle.demo.biz.DemoThingBiz;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务层（task）：演示与业务解耦的周期性任务，调用 biz 完成工作。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoThingTask {

    private final DemoThingBiz demoThingBiz;

    /**
     * 每分钟打印一次当前事物数量，仅作模板演示。
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void heartbeat() {
        try {
            log.info("[demo-task] current demo_thing count = {}", demoThingBiz.listAll().size());
        } catch (Exception e) {
            log.warn("[demo-task] heartbeat failed: {}", e.getMessage());
        }
    }

}
