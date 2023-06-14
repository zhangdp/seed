package com.zhangdp.seed.controller.sys;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2023/6/14
 *
 * @author zhangdp
 * @since
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/menu")
@Tag(name = "资源", description = "部门相关接口")
public class SysMenuController {
}
