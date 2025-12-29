package io.github.seed.model.dto;

import io.github.seed.model.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/5/27 资源树节点
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(title = "资源树节点")
public class ResourceTreeNode extends TreeNode<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 排序，升序
     */
    @Schema(title = "排序", description = "升序")
    private Integer sorts;

    /**
     * 权限标识
     */
    @Schema(title = "权限标识")
    private String permission;

    /**
     * 路由路径
     */
    @Schema(title = "路径", description = "前端菜单的路由路径")
    private String path;

    /**
     * 是否路由缓冲
     */
    @Schema(title = "是否路由缓冲", description = "0：否；1：是")
    private Integer isKeepAlive;
    /**
     * 是否展示
     */
    @Schema(title = "是否显示", description = "0：否；1：是")
    private Integer isVisible;
}
