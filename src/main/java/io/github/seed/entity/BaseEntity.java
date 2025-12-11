package io.github.seed.entity;

import com.mybatisflex.annotation.Id;
import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.constant.Const;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2023/4/3 实体基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public abstract class BaseEntity<T extends Serializable> implements Serializable {

    /**
     * 主键
     */
    @Id
    @Schema(title = "id", description = "修改时需传")
    @NotNull(message = "ID不能为空", groups = ValidGroup.Update.class)
    protected T id;
    /**
     * 创建时间
     */
    @Schema(title = "添加时间", description = "格式：" + Const.DATETIME_FORMATTER + "。保存时忽略")
    protected LocalDateTime createdAt;
    /**
     * 修改时间
     */
    @Schema(title = "修改时间", description = "格式：" + Const.DATETIME_FORMATTER + "。保存时忽略")
    protected LocalDateTime updatedAt;
}
