package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/4 角色
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_ROLE)
@Schema(title = "角色")
public class Role extends BaseLogicEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色标识
     */
    @Schema(title = "角色标识")
    private String code;
    /**
     * 角色名称
     */
    @Schema(title = "角色名称")
    private String name;
    /**
     * 描述
     */
    @Schema(title = "角色描述")
    private String description;

}
