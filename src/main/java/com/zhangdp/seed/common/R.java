package com.zhangdp.seed.common;

import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/3 全局接口返回对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@Schema(title = "响应信息")
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    @Schema(title = "状态码", description = CommonConst.RESULT_SUCCESS + "：成功，其它失败")
    private final int code;
    /**
     * 描述
     */
    @Schema(title = "描述")
    private final String message;
    /**
     * 数据体
     */
    @Schema(title = "数据体")
    private final T data;

    /**
     * 根据错误码枚举创建响应对象
     *
     * @param errorCode 错误码枚举
     */
    public R(ErrorCode errorCode) {
        this.code = errorCode.code();
        this.message = errorCode.message();
        this.data = null;
    }

    /**
     * 根据错误码枚举创建响应对象
     *
     * @param errorCode 错误码枚举
     * @param data      任意类型数据体
     */
    public R(ErrorCode errorCode, T data) {
        this.code = errorCode.code();
        this.message = errorCode.message();
        this.data = data;
    }

    /**
     * 根据状态码、描述常见对象
     *
     * @param code
     * @param message
     */
    public R(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    /**
     * 生成成功响应
     *
     * @return
     */
    public static R<?> success() {
        return new R<>(CommonConst.RESULT_SUCCESS, null, null);
    }

    /**
     * 生成成功返回
     *
     * @param message 描述
     * @param data    数据
     * @param <T>     任意类型数据体
     * @return 成功响应
     */
    public static <T> R<T> success(String message, T data) {
        return new R<>(CommonConst.RESULT_SUCCESS, message, data);
    }

    /**
     * 生成成功返回
     *
     * @param data 数据
     * @param <T>  任意类型数据体
     * @return 成功响应
     */
    public static <T> R<T> success(T data) {
        return new R<>(CommonConst.RESULT_SUCCESS, null, data);
    }

}
