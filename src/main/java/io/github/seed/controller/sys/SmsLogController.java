package io.github.seed.controller.sys;

import io.github.seed.common.annotation.Desensitization;
import io.github.seed.entity.sys.SmsLog;
import io.github.seed.model.PageData;
import io.github.seed.model.query.CursorPageQuery;
import io.github.seed.model.query.SmsLogQuery;
import io.github.seed.service.sys.SmsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信日志相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/sms/log")
@Tag(name = "短信日志", description = "短信日志相关接口")
public class SmsLogController {

    private final SmsLogService smsLogService;

    /**
     * 分页查询短信日志；手机号脱敏后返回
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询短信日志")
    @Desensitization
    public PageData<SmsLog> page(@RequestBody @Valid CursorPageQuery<SmsLogQuery> pageQuery) {
        return smsLogService.cursorQueryPage(pageQuery);
    }

}
