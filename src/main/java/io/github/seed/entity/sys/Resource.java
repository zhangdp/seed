package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/12 资源（菜单、按钮等）
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_RESOURCE)
@Schema(title = "资源")
public class Resource extends BaseLogicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源类型 （menu：菜单；button：按钮）
     */
    @NotNull(message = "类型不能为空")
    @Schema(title = "资源类型", description = "menu：菜单；button：按钮")
    private String type;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 32, message = "名称不能超过{max}个字符")
    @Schema(title = "名称")
    private String name;
    /**
     * 权限标识
     */
    @Schema(title = "权限标识")
    private String permission;
    /**
     * 父节点ID，根节点为0
     */
    @NotNull(message = "父ID不能为空")
    @Schema(title = "父id", description = "根节点为" + Const.ROOT_ID)
    private Long parentId;
    /**
     * 图标
     */
    @Schema(title = "图标")
    private String icon;
    /**
     * 路由路径
     */
    @Schema(title = "路径", description = "前端菜单的路由路径")
    private String path;
    /**
     * 描述
     */
    @Schema(title = "描述")
    private String description;
    /**
     * 同级排序值
     */
    @Schema(title = "同级排序值", description = "默认升序")
    private Integer sorts;
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
