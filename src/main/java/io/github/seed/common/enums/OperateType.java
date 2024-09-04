package io.github.seed.common.enums;

/**
 * 2023/4/17 操作类型
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum OperateType {

    /**
     * 查询
     */
    READ("read"),
    /**
     * 删除
     */
    DELETE("delete"),
    /**
     * 修改
     */
    UPDATE("update"),
    /**
     * 新增
     */
    CREATE("crete");

    private final String type;

    OperateType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
