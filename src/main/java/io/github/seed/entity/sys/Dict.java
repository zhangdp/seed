package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.LogicBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/12 字典
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_DICT)
@Schema(description = "字典")
public class Dict extends LogicBaseEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @Schema(title = "字典类型")
    private String type;
    /**
     * 字典名称
     */
    @Schema(title = "字典名称")
    private String name;
    /**
     * 描述
     */
    @Schema(title = "描述")
    private String description;
    /**
     * 是否是系统内置
     */
    @Schema(title = "系统内置", description = "系统内置的不允许页面修改。1：是；0：否")
    private Integer isSystem;
}
