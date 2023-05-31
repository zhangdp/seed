package com.zhangdp.seed.common.enums;

/**
 * 2023/5/18 错误码枚举
 * <br>规定错误码由5位数字组成，分成两个部分：1位数字表示错误产生来源+四位数字编号
 * <ul>
 *     <li>第1位数字表示错误来源，1：用户错误，如参数错误、未登录、权限不足等；2：业务错误，如库存不足无法下单等；
 *     3：系统错误，如程序运行错误；4：中间件错误，如数据库、redis等服务不可用；5：第三方服务错误，比如调用第三方支付接口出错等</li>
 *     <li>4位数字表示错误编号，范围0000-9999，先到先得，可继续划分大类，大类之间的步长间距预留100</li>
 * </ul>
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum ErrorCode {

    /**
     * 1开头的错误码，错误来源是用户
     */
    BAD_REQUEST(10000, "非法请求"),
    /**
     * 未登录
     */
    UNAUTHORIZED(10001, "未登录"),
    /**
     * 未登录-token不存在
     */
    TOKEN_NOT_FOUND(10002, "凭证不存在"),
    /**
     * 未登录-token无效
     */
    TOKEN_INVALID(10003, "凭证无效"),
    /**
     * 未登录-token已过期
     */
    TOKEN_EXPIRED(10004, "凭证已过期"),
    /**
     * 未登录-顶号
     */
    TOKEN_REPLACED(10005, "已被顶下线"),
    /**
     * 未登录-已被踢下线
     */
    TOKEN_KICK_OUT(10006, "已被踢下线"),
    /**
     * 未登录-无Basic认证
     */
    NOT_BASIC_AUTH(10010, "无Basic认证"),
    /**
     * 权限不足
     */
    FORBIDDEN(10100, "权限不足"),
    /**
     * 权限不足-不满足指定角色
     */
    FORBIDDEN_NO_ROLE(10101, "角色不满足"),
    /**
     * 权限不足-不满足指定权限
     */
    FORBIDDEN_NO_PERMISSION(10102, "无权限"),
    /**
     * 资源不存在
     */
    NOT_FOUND(11000, "资源不存在"),
    /**
     * 参数校验失败
     */
    PARAMS_VALID_FAILED(12000, "参数错误"),
    /**
     * 缺少参数
     */
    PARAMETER_MISSING(12001, "缺少参数"),
    /**
     * 参数类型不符合
     */
    PARAMETER_TYPE_ERROR(12002, "参数类型不符合"),


    /**
     * 2开头的错误码，错误来源是业务
     */
    BIZ_ERROR(20000, "请求错误"),
    /**
     * 账号重复
     */
    USERNAME_REPEAT(20001, "账号重复"),
    /**
     * 部门不存在
     */
    DEPT_NOT_EXISTS(20100, "部门不存在"),
    /**
     * 父部门不存在
     */
    DEPT_PARENT_NOT_EXISTS(20101, "父部门不存在"),


    /**
     * 3开头的错误码，错误来源是系统
     */
    SERVER_ERROR(30000, "系统错误"),


    /**
     * 4开头的错误码，错误来源是中间件
     */
    SQL_ERROR(40000, "系统错误"),
    /**
     * 查询IP地理位置失败
     */
    SEARCH_IP_REGION_FAILED(40100, "查询IP地理位置失败"),
    ;
    /**
     * 状态码
     */
    private final int code;
    /**
     * 错误描述
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return
     */
    public int code() {
        return this.code;
    }

    /**
     * 获取错误描述
     *
     * @return
     */
    public String message() {
        return this.message;
    }

    @Override
    public String toString() {
        return "[" + this.code + "]" + this.message;
    }
}
