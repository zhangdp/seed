package com.zhangdp.seed.common;

import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "全局响应信息")
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    @Schema(title = "状态码", description = CommonConst.RESULT_SUCCESS + "：成功，其它失败")
    private int code;
    /**
     * 描述
     */
    @Schema(title = "描述")
    private String msg;
    /**
     * 数据体
     */
    @Schema(title = "数据体")
    private T data;

    /**
     * 生成成功返回
     *
     * @return
     */
    public static R<?> success() {
        return new R<>(CommonConst.RESULT_SUCCESS, null, null);
    }

    /**
     * 生成成功返回
     *
     * @param msg  描述
     * @param data 数据
     * @param <T>  任意类型
     * @return 成功返回
     */
    public static <T> R<T> success(String msg, T data) {
        return new R<>(CommonConst.RESULT_SUCCESS, msg, data);
    }

    /**
     * 生成成功返回
     *
     * @param data 数据
     * @param <T>  任意类型
     * @return 成功返回
     */
    public static <T> R<T> success(T data) {
        return new R<>(CommonConst.RESULT_SUCCESS, null, data);
    }

    /**
     * 生成失败返回
     *
     * @param msg 描述
     * @return 错误返回
     */
    public static R<?> fail(String msg) {
        return new R<>(CommonConst.RESULT_FAIL, msg, null);
    }

}
