package com.zhangdp.seed.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangdp.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/12 系统参数
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_param")
@Schema(description = "系统参数")
public class SysParam extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标识
     */
    @Schema(title = "标识")
    private String code;
    /**
     * 描述
     */
    @Schema(title = "描述")
    private String description;
    /**
     * 值
     */
    @Schema(title = "值")
    private String value;
    /**
     * 值是否加密
     */
    @Schema(title = "值是否加密", description = "1：是；0：否")
    private Integer isEncrypted;
    /**
     * 是否系统内置
     */
    @Schema(title = "是否系统内置", description = "系统内置的不允许页面修改。1：是；0：否")
    private Integer isSystem;
}
