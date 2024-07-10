package com.zhangdp.seed.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangdp.seed.entity.LogicBaseEntity;
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
@TableName("sys_dict")
@Schema(description = "字典")
public class SysDict extends LogicBaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @Schema(title = "字典类型")
    private String type;
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
