package io.github.seed.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色权限分配入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "角色权限分配入参")
public class RolePermissionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空")
    @Schema(title = "角色id")
    private Long roleId;

    /**
     * 权限id列表，传空数组表示清空权限
     */
    @NotNull(message = "权限id列表不能为空")
    @Schema(title = "权限id列表")
    private List<Long> permissionIds;
}
