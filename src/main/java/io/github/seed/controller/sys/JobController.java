package io.github.seed.controller.sys;

import io.github.seed.entity.sys.Job;
import io.github.seed.manager.JobManager;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobQuery;
import io.github.seed.model.query.PageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务相关接口
 * <br>任务的启停走单独的{@code status}接口，不用修改接口顺带改状态，因为启用要重算下次触发时间
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/job")
@Tag(name = "定时任务", description = "定时任务的增删改查、启停与手动执行")
public class JobController {

    private final JobManager jobManager;

    /**
     * 分页查询任务
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询定时任务")
    public PageData<Job> page(@RequestBody @Valid PageQuery<JobQuery> pageQuery) {
        return jobManager.queryPage(pageQuery);
    }

    /**
     * 查询单个任务
     *
     * @param id 任务id
     * @return 任务
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询定时任务")
    public Job get(@PathVariable Long id) {
        return jobManager.getById(id);
    }

    /**
     * 新增任务，新增后默认是已停止状态，确认无误再启用
     *
     * @param job 任务
     * @return 是否新增成功
     */
    @PostMapping
    @Operation(summary = "新增定时任务")
    public boolean add(@RequestBody @Valid Job job) {
        return jobManager.add(job);
    }

    /**
     * 修改任务，状态与下次触发时间不在这里改
     *
     * @param job 任务
     * @return 是否修改成功
     */
    @PutMapping
    @Operation(summary = "修改定时任务")
    public boolean update(@RequestBody @Valid Job job) {
        return jobManager.update(job);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除定时任务")
    public boolean delete(@PathVariable Long id) {
        return jobManager.delete(id);
    }

    /**
     * 启用或停止任务；启用时从当前时间推算下次触发时间
     *
     * @param id    任务id
     * @param start true启用、false停止
     * @return 是否更新成功
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "启用或停止定时任务")
    public boolean changeStatus(@PathVariable Long id, @RequestParam boolean start) {
        return jobManager.changeStatus(id, start);
    }

    /**
     * 立即执行一次，不影响任务自身的启用状态与触发计划
     *
     * @param id 任务id
     * @return 是否执行成功
     */
    @PostMapping("/{id}/run")
    @Operation(summary = "立即执行一次定时任务")
    public boolean runOnce(@PathVariable Long id) {
        return jobManager.runOnce(id);
    }

}
