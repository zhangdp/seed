package io.github.seed.controller.log;

import io.github.seed.entity.log.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.log.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2024/10/8 操作日志相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/log/operation")
@Tag(name = "操作日志", description = "操作日志相关接口")
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询操作日志")
    public PageData<OperationLog> page(@RequestBody @Valid PageQuery<OperationLogQuery> pageQuery) {
        return operationLogService.queryPage(pageQuery);
    }

}
