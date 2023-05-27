package com.zhangdp.seed.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/3 部门表
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_dept")
@Schema(description = "部门")
public class SysDept extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @Schema(title = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    @Length(max = 30, message = "部门名称最多30个字符")
    private String name;
    /**
     * 父级部门id
     */
    @Schema(title = "父级部门id", description = "根部门则为" + CommonConst.ROOT_ID)
    @NotNull(message = "父级部门id不能为空")
    @Min(value = 0, message = "父级部门id不合法")
    private Long parentId;
    /**
     * 同级排序，升序
     */
    @Schema(title = "排序", description = "同级排序，升序")
    private Integer sorts;

}
