package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/12/23 选择节点
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "选择节点")
public abstract class SelectNode<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 值
     */
    @Schema(title = "值")
    protected T value;
    /**
     * 标签
     */
    @Schema(title = "标签")
    protected String label;
    /**
     * 图标
     */
    @Schema(title = "图标")
    protected String icon;
    /**
     * 描述
     */
    @Schema(title = "描述")
    protected String description;
    /**
     * 是否默认选中，默认false
     */
    @Schema(title = "是否默认选中，默认false")
    protected Boolean selected = false;
    /**
     * 是否不可选，默认false
     */
    @Schema(title = "是否不可选，默认false")
    protected Boolean disabled = false;

    public SelectNode(T value) {
        this.value = value;
    }

    public SelectNode(T value, String label) {
        this.value = value;
        this.label = label;
    }

}
