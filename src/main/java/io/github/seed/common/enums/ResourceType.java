package io.github.seed.common.enums;

/**
 * 2023/6/21 资源类型
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum ResourceType {

    /**
     * 菜单
     */
    MENU("menu"),
    /**
     * 按钮
     */
    BUTTON("button");

    private final String type;

    ResourceType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
