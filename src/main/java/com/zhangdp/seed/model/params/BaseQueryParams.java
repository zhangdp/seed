package com.zhangdp.seed.model.params;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/6/21 基本查询对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class BaseQueryParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 查询内容
     */
    protected String query;
}
