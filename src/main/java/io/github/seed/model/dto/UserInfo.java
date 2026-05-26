package io.github.seed.model.dto;

import io.github.seed.entity.sys.Dept;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 2023/5/17 用户信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Schema(title = "用户信息")
public class UserInfo extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @Schema(title = "部门")
    private Dept dept;
    /**
     * 拥有的角色列表
     */
    @Schema(title = "拥有的角色列表")
    private List<Role> roles;

    /**
     * 部门名称
     *
     * @return
     */
    // @Schema(title = "部门名称")
    public String deptName() {
        return dept == null ? null : dept.getName();
    }

    /**
     * 角色名称列表
     *
     * @return
     */
    // @Schema(title = "角色名称列表")
    public List<String> roleNames() {
        return roles == null || roles.isEmpty() ? Collections.emptyList() : roles.stream().map(Role::getName).toList();
    }

    /**
     * 角色标识列表
     *
     * @return
     */
    // @Schema(title = "角色标识列表")
    public List<String> roleCodes() {
        return roles == null || roles.isEmpty() ? Collections.emptyList() : roles.stream().map(Role::getCode).toList();
    }

}
