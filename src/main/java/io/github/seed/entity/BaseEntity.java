package io.github.seed.entity;

import com.mybatisflex.annotation.Id;
import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.constant.Const;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    /**
     * 主键
     */
    @Id
    @Schema(description = "ID，修改时需传")
    @NotNull(message = "ID不能为空", groups = ValidGroup.Update.class)
    protected Long id;
    /**
     * 创建时间
     */
    @Schema(description = "添加时间，格式：" + Const.DATETIME_FORMATTER + "。保存时忽略")
    protected LocalDateTime createdAt;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间，格式：" + Const.DATETIME_FORMATTER + "。保存时忽略")
    protected LocalDateTime updatedAt;
}
