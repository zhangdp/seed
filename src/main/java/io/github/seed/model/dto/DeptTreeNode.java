package io.github.seed.model.dto;

import io.github.seed.model.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/5/27 部门树节点
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(title = "部门树节点")
public class DeptTreeNode extends TreeNode<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 排序，升序
     */
    @Schema(title = "排序", description = "升序")
    private Integer sorts;

}
