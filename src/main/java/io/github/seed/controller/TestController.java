package io.github.seed.controller;

import io.github.seed.common.annotation.LogOperation;
import io.github.seed.common.annotation.NoJsonTrim;
import io.github.seed.common.annotation.PublishEvent;
import io.github.seed.common.constant.EventConst;
import io.github.seed.common.enums.OperateType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 2023/8/25 测试接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @PostMapping("/helloWorld")
    @LogOperation(type = OperateType.READ, title = "测试", refModule = "test", refIdEl = "#data.id")
    @PublishEvent(EventConst.TEST)
    public TestData helloWorld(@RequestBody @Valid TestData data) {
        log.info("测试：{}", data);
        return data;
    }

    @Data
    public static class TestData {
        @NotNull
        private Long id;
        @NotBlank
        private String name;
        @NoJsonTrim
        private String description;
        private LocalDate localDate;
        private LocalTime localTime;
        private LocalDateTime localDateTime;
        private Date date;
    }
}
