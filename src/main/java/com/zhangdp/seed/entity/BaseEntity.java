package com.zhangdp.seed.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 2023/4/3 entity基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public abstract class BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(title = "id", description = "修改时需传")
    @NotNull(message = "id不能为空", groups = ValidGroup.Update.class)
    protected Long id;
    /**
     * 逻辑删除，0：否，默认；1：已删除
     */
    @TableLogic
    @Schema(title = "逻辑删除", description = "0：否，默认；1：已删除", hidden = true)
    @JsonIgnore
    protected Integer isDeleted;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "添加时间", description = "格式：" + CommonConst.DATETIME_FORMATTER)
    protected LocalDateTime addTime;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @Schema(title = "修改时间", description = "格式：" + CommonConst.DATETIME_FORMATTER)
    protected LocalDateTime updateTime;
    /**
     * 创建用户id
     */
    // @Schema(title = "创建人id")
    // protected Long addUserId;
    /**
     * 修改用户id
     */
    // @Schema(title = "修改人id")
    // protected Long updateUserId;
}
