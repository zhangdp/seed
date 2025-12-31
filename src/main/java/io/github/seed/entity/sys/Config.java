package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_CONFIG)
@Schema(description = "系统配置")
public class Config extends BaseLogicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标识
     */
    @Schema(description = "标识")
    @NotBlank(message = "配置key不能为空")
    @Length(max = 32, message = "配置key最多{max}个字符")
    private String configKey;
    /**
     * 描述
     */
    @Schema(description = "描述")
    @Length(max = 255, message = "描述最多{max}个字符")
    private String description;
    /**
     * 值
     */
    @Schema(description = "配置值")
    @NotBlank(message = "配置值不能为空")
    @Length(max = 255, message = "配置值最多{max}个字符")
    private String configValue;
    /**
     * 值是否加密
     */
    @Schema(description = "值是否加密，1：是；0：否，默认")
    @Range(min = 0, max = 1, message = "是否加密只能为{min}或者{max}")
    private Integer isEncrypted;
    /**
     * 是否系统内置
     */
    @Schema(description = "是否系统内置，1：是；0：否，默认")
    @Range(min = 0, max = 1, message = "是否系统内置只能为{min}或者{max}")
    private Integer isSystem;
}
