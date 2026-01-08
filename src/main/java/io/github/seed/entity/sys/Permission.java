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
 * 权限（菜单、按钮等）
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_PERMISSION)
@Schema(description = "资源")
public class Permission extends BaseLogicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源类型 （menu：菜单；button：按钮）
     */
    @NotNull(message = "资源类型不能为空")
    @Schema(description = "资源类型，menu：菜单；button：按钮")
    private String type;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 32, message = "名称不能超过{max}个字符")
    @Schema(description = "名称")
    private String name;
    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String permission;
    /**
     * 父节点ID，根节点为0
     */
    @NotNull(message = "父ID不能为空")
    @Schema(description = "父节点ID，根节点为" + Const.ROOT_ID)
    private Long parentId;
    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;
    /**
     * 路由路径
     */
    @Schema(description = "前端菜单的路由路径")
    private String path;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
    /**
     * 同级排序，升序
     */
    @Schema(description = "同级排序，升序")
    private Integer sorts;
    /**
     * 是否路由缓冲
     */
    @Schema(description = "是否路由缓冲，0：否；1：是")
    private Integer isKeepAlive;
    /**
     * 是否显示，0：否；1：是，默认
     */
    @Schema(description = "是否显示，0：否；1：是，默认")
    private Integer isVisible;

}
