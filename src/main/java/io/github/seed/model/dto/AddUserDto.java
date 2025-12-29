package io.github.seed.model.dto;

import io.github.seed.entity.sys.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 添加用户入参
 *
 * @author zhangdp
 * @since 2025/9/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Schema(title = "添加用户")
public class AddUserDto extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色id列表
     */
    @Schema(title = "角色id列表")
    @NotEmpty(message = "角色不能为空")
    private Set<Long> roleIds;
}
