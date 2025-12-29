package io.github.seed.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 2025/12/11 带有逻辑删除和创建者、修改者字段的实体基类
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
    @Schema(title = "创建人")
    protected Long createdBy;
    /**
     * 最后修改人
     */
    @Schema(title = "最后修改人")
    protected Long updatedBy;
}
