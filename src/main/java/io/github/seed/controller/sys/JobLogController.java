package io.github.seed.controller.sys;

import io.github.seed.entity.sys.JobLog;
import io.github.seed.manager.JobManager;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobLogQuery;
import io.github.seed.model.query.PageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务执行日志相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/jobLog")
@Tag(name = "定时任务日志", description = "定时任务执行日志的查询与清理")
public class JobLogController {

    private final JobManager jobManager;

    /**
     * 分页查询执行日志
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询定时任务执行日志")
    public PageData<JobLog> page(@RequestBody @Valid PageQuery<JobLogQuery> pageQuery) {
        return jobManager.queryLogPage(pageQuery);
    }

    /**
     * 清空执行日志，不传jobId时清空全部
     *
     * @param jobId 任务id，为空表示全部
     * @return 删除的条数
     */
    @DeleteMapping
    @Operation(summary = "清空定时任务执行日志")
    public int clear(@RequestParam(required = false) Long jobId) {
        return jobManager.clearLog(jobId);
    }

}
