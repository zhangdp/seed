package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 2023/5/27 树节点模型
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@Schema(title = "树节点")
public abstract class TreeNode<T> extends SelectNode<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否叶子节点
     */
    @Schema(title = "是否叶子节点")
    protected Boolean isLeaf = false;
    /**
     * 父
     */
    @Schema(title = "父")
    protected T parent;
    /**
     * 孩子节点列表
     */
    @Schema(title = "孩子节点")
    protected List<TreeNode<T>> children;
}
