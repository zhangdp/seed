package com.zhangdp.seed.common;

import com.zhangdp.seed.common.constant.CommonConsts;
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
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;
    /**
     * 描述
     */
    private String msg;
    /**
     * 消息体
     */
    private T data;

    /**
     * 生成成功返回
     *
     * @param msg  描述
     * @param data 数据
     * @param <T>  任意类型
     * @return 成功返回
     */
    public static <T> R<T> success(String msg, T data) {
        return new R<>(CommonConsts.RESULT_SUCCESS, msg, data);
    }

    /**
     * 生成成功返回
     *
     * @param data 数据
     * @param <T>  任意类型
     * @return 成功返回
     */
    public static <T> R<T> success(T data) {
        return new R<>(CommonConsts.RESULT_SUCCESS, null, data);
    }

    /**
     * 生成失败返回
     *
     * @param msg 描述
     * @return 错误返回
     */
    public static R<?> fail(String msg) {
        return new R<>(CommonConsts.RESULT_FAIL, msg, null);
    }

}
