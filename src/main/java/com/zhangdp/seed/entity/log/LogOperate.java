package com.zhangdp.seed.entity.log;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2023/4/17 操作日志
 *
 * @author zhangdp
 * @since
 */
@Data
@Accessors(chain = true)
@TableName("log_operate")
@Schema(description = "操作日志")
public class LogOperate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    @Schema(title = "id")
    private Long id;
    /**
     * 登陆时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "登陆时间", description = "格式：" + CommonConst.DATETIME_FORMATTER)
    private LocalDateTime createTime;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;
    /**
     * 请求uri
     */
    @Schema(title = "用户id")
    private String uri;
    /**
     * 请求的http 方式
     *
     * @return
     */
    @Schema(title = "http方式")
    private String httpMethod;
    /**
     * 执行的方法
     */
    @Schema(title = "执行的方法")
    private String method;
    /**
     * 耗时
     */
    @Schema(title = "耗时")
    private Long costTime;
    /**
     * 关联模块类型
     */
    @Schema(title = "关联模块类型")
    private String refModule;
    /**
     * 关联模块id
     */
    @Schema(title = "关联模块id")
    private Long refId;
    /**
     * 返回结果标识
     */
    @Schema(title = "返回结果标识")
    private Long resultCode;
    /**
     * json格式的返回值
     */
    @Schema(title = "json格式的返回值")
    private String jsonResult;
    /**
     * json格式的入参
     */
    @Schema(title = "json格式的入参")
    private String jsonParams;
    /**
     * 异常堆栈
     */
    @Schema(title = "异常堆栈")
    private String errorTrace;
}
