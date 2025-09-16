package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/12 角色关联资源
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_ROLE_RESOURCE)
public class RoleResource extends BaseEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源id
     */
    private Long resourceId;
    /**
     * 角色id
     */
    private Long roleId;
}
