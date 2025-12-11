package io.github.seed.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 2025/12/11 带有逻辑删除和创建者、修改者字段的实体基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BaseLogicAuditableEntity<T extends Serializable> extends BaseLogicEntity<T> implements Serializable {

    /**
     * 创建人
     */
    @Schema(title = "创建人")
    protected Long createdBy;
    /**
     * 最后修改人
     */
    @Schema(title = "最后修改人")
    protected Long updatedBy;
}
