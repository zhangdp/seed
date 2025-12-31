package io.github.seed.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 带有逻辑删除字段的实体基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class BaseLogicEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 逻辑删除，0：否，默认；1：已删除
     */
    @Column(isLogicDelete = true)
    @Schema(description = "逻辑删除，0：否，默认；1：已删除", hidden = true)
    @JsonIgnore
    protected Integer deleted;

}
