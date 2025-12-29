package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 2024/8/19 批量处理结果
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "批量处理结果")
public class BatchResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总数
     */
    @Schema(title = "总数")
    private int total;
    /**
     * 成功数
     */
    @Schema(title = "成功数")
    private int success;
    /**
     * 错误数
     */
    @Schema(title = "错误数")
    private int error;
    /**
     * 跳过数
     */
    @Schema(title = "跳过数")
    private int skip;
    /**
     * 总耗时
     */
    @Schema(title = "总耗时")
    private long costTime;
    /**
     * 开始执行时间
     */
    @Schema(title = "开始执行时间")
    private LocalDateTime startTime;
    /**
     * 完成时间
     */
    @Schema(title = "完成时间")
    private LocalDateTime finishTime;
    /**
     * 错误日志描述列表
     */
    @Schema(title = "错误日志描述列表")
    private List<String> errorMessages;

    public BatchResult() {
        this(0, LocalDateTime.now());
    }

    public BatchResult(LocalDateTime startTime) {
        this(0, startTime);
    }

    public BatchResult(int total) {
        this(total, LocalDateTime.now());
    }

    public BatchResult(int total, LocalDateTime startTime) {
        this.total = total;
        this.startTime = startTime;
    }

    /**
     * 开始
     *
     * @return
     */
    public static BatchResult start() {
        return new BatchResult();
    }

    /**
     * 开始
     *
     * @param total
     * @return
     */
    public static BatchResult start(int total) {
        return new BatchResult(total);
    }

    /**
     * 结束
     *
     * @return
     */
    public BatchResult finish() {
        this.finishTime = LocalDateTime.now();
        this.costTime = ChronoUnit.MILLIS.between(startTime, finishTime);
        return this;
    }

    /**
     * 成功数+1
     *
     * @return
     */
    public BatchResult plusSuccess() {
        return plusSuccess(1);
    }

    /**
     * 成功数+n
     *
     * @param n
     * @return
     */
    public BatchResult plusSuccess(int n) {
        this.success += n;
        return this;
    }

    /**
     * 失败数+1
     *
     * @return
     */
    public BatchResult plusError() {
        plusError(1);
        return this;
    }

    /**
     * 失败数+n
     *
     * @param n
     * @return
     */
    public BatchResult plusError(int n) {
        this.error += n;
        return this;
    }

    /**
     * 跳过数+1
     *
     * @return
     */
    public BatchResult plusSkip() {
        plusSkip(1);
        return this;
    }

    /**
     * 跳过数+n
     *
     * @param n
     * @return
     */
    public BatchResult plusSkip(int n) {
        this.skip += n;
        return this;
    }

    /**
     * 新增错误描述
     *
     * @param errorMessage
     * @return
     */
    public BatchResult addErrorMessage(String errorMessage) {
        this.initErrorMessages();
        this.errorMessages.add(errorMessage);
        return this;
    }

    /**
     * 批量新增错误描述
     *
     * @param errorMessages
     * @return
     */
    public BatchResult batchAddErrorMessages(Collection<String> errorMessages) {
        this.initErrorMessages();
        if (errorMessages != null && !errorMessages.isEmpty()) {
            this.errorMessages.addAll(errorMessages);
        }
        return this;
    }

    /**
     * 初始化错误描述字段
     */
    private void initErrorMessages() {
        if (this.errorMessages == null) {
            this.errorMessages = new ArrayList<>(this.total > 0 ? total : 10);
        }
    }
}
