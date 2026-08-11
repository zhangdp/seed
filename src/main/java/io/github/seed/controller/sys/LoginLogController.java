package io.github.seed.controller.sys;

import io.github.seed.entity.sys.LoginLog;
import io.github.seed.model.PageData;
import io.github.seed.model.query.CursorPageQuery;
import io.github.seed.model.query.LoginLogQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2024/10/8 登录日志相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/loginLog")
@Tag(name = "登录日志", description = "登录日志相关接口")
public class LoginLogController {

    private final LoginLogService loginLogService;

    /**
     * 分页查询登录日志
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询登录日志")
    public PageData<LoginLog> page(@RequestBody @Valid CursorPageQuery<LoginLogQuery> pageQuery) {
        return loginLogService.cursorQueryPage(pageQuery);
    }

}
