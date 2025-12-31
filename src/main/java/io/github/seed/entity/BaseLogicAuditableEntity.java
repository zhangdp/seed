package io.github.seed.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 带有逻辑删除和创建者、修改者字段的实体基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class BaseLogicAuditableEntity extends BaseLogicEntity implements Serializable {

    /**
     * 创建人
     */
    @Schema(description = "创建人ID")
    protected Long createdBy;
    /**
     * 最后修改人
     */
    @Schema(description = "最后修改人ID")
    protected Long updatedBy;
}
