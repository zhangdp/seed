package com.zhangdp.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 2023/5/27 树节点模型
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Schema(title = "树节点")
public class TreeNode<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id")
    protected T id;
    /**
     * 文本名称
     */
    @Schema(title = "名称")
    protected String label;
    /**
     * 父id
     */
    @Schema(title = "父id")
    protected T parentId;
    /**
     * 孩子节点列表
     */
    @Schema(title = "孩子节点")
    protected List<TreeNode<T>> childs;
}
