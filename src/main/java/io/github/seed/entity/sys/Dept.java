package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门表
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_DEPT)
@Schema(description = "部门")
public class Dept extends BaseLogicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    @Length(max = 30, message = "部门名称最多30个字符")
    private String name;
    /**
     * 父级部门id
     */
    @Schema(description = "父级部门id，根部门则为" + Const.ROOT_ID)
    @NotNull(message = "父级部门id不能为空")
    @Min(value = 0, message = "父级部门id不合法")
    private Long parentId;
    /**
     * 同级排序，升序
     */
    @Schema(description = "同级排序，升序")
    private Integer sorts;

}
