package io.github.seed.controller;

import io.github.seed.common.annotation.OperateLog;
import io.github.seed.common.enums.OperateType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 2023/8/25 测试接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/helloWorld")
    @OperateLog(type = OperateType.READ, title = "测试", refModule = "test", refIdEl = "#id")
    public Map<String, Object> helloWorld(String name, Long id, String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("args", args);
        map.put("name", name);
        map.put("id", id);
        map.put("date", new Date());
        map.put("localDate", LocalDate.now());
        map.put("localTime", LocalTime.now());
        map.put("localDateTime", LocalDateTime.now());
        return map;
    }
}
