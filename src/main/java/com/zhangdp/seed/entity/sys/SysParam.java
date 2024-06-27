package com.zhangdp.seed.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.entity.LogicBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

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
@TableName(TableNameConst.SYS_PARAM)
@Schema(description = "系统参数")
public class SysParam extends LogicBaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标识
     */
    @Schema(title = "标识")
    @NotBlank(message = "参数标识不能为空")
    @Length(max = 32, message = "参数标识最多{max}个字符")
    private String code;
    /**
     * 描述
     */
    @Schema(title = "描述")
    @Length(max = 255, message = "描述最多{max}个字符")
    private String description;
    /**
     * 值
     */
    @Schema(title = "参数值")
    @NotBlank(message = "参数值不能为空")
    @Length(max = 255, message = "参数值最多{max}个字符")
    private String value;
    /**
     * 值是否加密
     */
    @Schema(title = "值是否加密", description = "1：是；0：否。默认0")
    @Range(min = 0, max = 1, message = "是否加密只能为{min}-{max}")
    private Integer isEncrypted;
    /**
     * 是否系统内置
     */
    @Schema(title = "是否系统内置", description = "系统内置的不允许页面修改。1：是；0：否。默认0")
    @Range(min = 0, max = 1, message = "是否系统内置只能为{min}-{max}")
    private Integer isSystem;
}
