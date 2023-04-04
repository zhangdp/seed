package com.zhangdp.seed.controller;

import com.zhangdp.seed.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 测试接口
 *
 * @author zhangdp
 * @since 1.0.0
 * @date 2023/4/3
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试接口
     * @return
     */
    @GetMapping("/hello")
    public R<Map<String, Object>> hello() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("localDateTime", LocalDateTime.now());
        map.put("date", new Date());
        map.put("localDate", LocalDate.now());
        map.put("localTime", LocalTime.now());
        return new R<>(0, null, map);
    }
}
