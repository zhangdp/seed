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
    MENU(1),
    /**
     * 按钮
     */
    BUTTON(2);

    private final int type;

    ResourceType(int type) {
        this.type = type;
    }

    public int type() {
        return this.type;
    }
}
