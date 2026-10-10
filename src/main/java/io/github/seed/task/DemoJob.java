package io.github.seed.task;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 示例任务：演示定时任务怎么写
 * <br>任务表里{@code heartbeatDemoJob}指向本类的{@code tick}，默认停用，可在任务列表里启用或手动执行一次
 * <br>业务任务照此写：一个spring bean + 一个public方法，方法参数留空即可
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class DemoJob {

    /**
     * 打点
     */
    public void tick() {
        log.info("示例任务执行：{}", LocalDateTime.now());
    }

    /**
     * 带参数的示例，参数在任务表里以json数组填写，如["hello"]
     *
     * @param word 自定义内容
     */
    public void sayHello(String word) {
        log.info("示例任务执行：hello {}", word);
    }
}
