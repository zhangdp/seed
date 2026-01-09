package io.github.seed.common.data;

import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局接口返回对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(description = "响应信息")
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    @Schema(description = "状态码" + Const.RESULT_SUCCESS + "：成功，其它失败")
    private final int code;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private final String message;
    /**
     * 数据体
     */
    @Schema(description = "数据体")
    private final T data;

    /**
     * 根据状态码、描述创建对象
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
     * 根据状态码、描述、数据体创建对象
     *
     * @param code
     * @param message
     * @param data
     */
    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 生成成功响应
     *
     * @return
     */
    public static R<?> success() {
        return new R<>(Const.RESULT_SUCCESS, "", null);
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
        return new R<>(Const.RESULT_SUCCESS, message, data);
    }

    /**
     * 生成成功返回
     *
     * @param data 数据
     * @param <T>  任意类型数据体
     * @return 成功响应
     */
    public static <T> R<T> success(T data) {
        return new R<>(Const.RESULT_SUCCESS, "", data);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码枚举
     * @return
     */
    public static R<?> fail(ErrorCode errorCode) {
        return new R<>(errorCode.code(), errorCode.message(), null);
    }

    /**
     * 根据错误码枚举创建响应对象
     *
     * @param errorCode 错误码枚举
     * @param data      任意类型数据体
     * @return
     */
    public static <T> R<T> fail(ErrorCode errorCode, T data) {
        return new R<>(errorCode.code(), errorCode.message(), data);
    }

}
