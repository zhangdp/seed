package io.github.seed.controller;

import cn.hutool.v7.core.thread.ThreadUtil;
import cn.hutool.v7.core.util.RandomUtil;
import io.github.seed.common.annotation.RecordOperationLog;
import io.github.seed.common.annotation.NoJsonTrim;
import io.github.seed.common.annotation.PublishEvent;
import io.github.seed.common.constant.EventConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.enums.OperateType;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.util.JsonUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 测试接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @PostMapping("/helloWorld")
    @RecordOperationLog(type = OperateType.READ, description = "测试", refModule = "test", refIdEl = "#data.id", recordIfError = true)
    @PublishEvent(EventConst.TEST)
    public TestData helloWorld(@RequestBody @Valid TestData data) {
        log.info("测试：{}", data);
        // 模拟处理耗时，随机10-100毫秒
        ThreadUtil.sleep(RandomUtil.randomInt(10, 100));
        if (data.getId() % 2 == 0) {
            throw new RuntimeException("测试失败");
        }
        return data;
    }

    @Data
    public static class TestData {
        @NotNull
        private long id;
        @NotBlank
        private String name;
        @NoJsonTrim
        private String description;
        private LocalDate localDate = LocalDate.now();
        private LocalTime localTime = LocalTime.now();
        private LocalDateTime localDateTime = LocalDateTime.now();
        private Date date = new Date();
    }
}
