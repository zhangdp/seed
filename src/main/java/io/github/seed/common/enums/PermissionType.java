package io.github.seed.common.enums;

/**
 * 权限类型
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum PermissionType {

    /**
     * 菜单
     */
    MENU("menu"),
    /**
     * 按钮
     */
    BUTTON("button");

    private final String type;

    PermissionType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
