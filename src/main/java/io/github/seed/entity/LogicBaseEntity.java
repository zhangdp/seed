package io.github.seed.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 2023/4/3 带有逻辑删除字段的entity基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class LogicBaseEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 逻辑删除，0：否，默认；1：已删除
     */
    @TableLogic
    @Schema(title = "逻辑删除", description = "0：否，默认；1：已删除", hidden = true)
    @JsonIgnore
    protected Integer isDeleted;

}
