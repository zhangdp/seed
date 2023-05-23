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
     * 权限不足
     */
    FORBIDDEN(10002, "权限不足"),
    /**
     * 资源不存在
     */
    NOT_FOUND(10003, "资源不存在"),
    /**
     * 参数校验失败
     */
    VALID_PARAMS_FAILED(10400, "参数校验失败"),


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
    DEPT_NOT_EXISTS(20002, "部门不存在"),


    /**
     * 3开头的错误码，错误来源是系统
     */
    SERVER_ERROR(30000, "系统错误"),


    /**
     * 4开头的错误码，错误来源是中间件
     */
    SQL_ERROR(41000, "系统错误"),

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
