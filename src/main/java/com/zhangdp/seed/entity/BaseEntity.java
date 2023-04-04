package com.zhangdp.seed.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2023/4/3 资源类型entity基类
 *
 * @author zhangdp
 * @since
 */
@Data
@Accessors(chain = true)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    @Schema(title = "id", description = "修改时需传")
    @NotNull(message = "id不能为空", groups = ValidGroup.Update.class)
    protected Long id;
    /**
     * 逻辑删除，0：否，默认；1：已删除
     */
    @TableLogic
    @Schema(title = "逻辑删除", description = "0：否，默认；1：已删除", hidden = true)
    private Integer delFlag;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "创建时间", description = "格式：" + CommonConst.DATETIME_PATTERN)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(title = "修改时间", description = "格式：" + CommonConst.DATETIME_PATTERN)
    private LocalDateTime updateTime;
    /**
     * 创建用户id
     */
    @Schema(title = "创建人id")
    private Long createUserId;
    /**
     * 修改用户id
     */
    @Schema(title = "修改人id")
    private Long updateUserId;
}
