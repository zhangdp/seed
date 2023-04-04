package com.zhangdp.seed.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
    protected Long id;
    /**
     * 逻辑删除，0：否，默认；1：已删除
     */
    @TableLogic
    private Integer delFlag;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 创建用户id
     */
    private Long createUserId;
    /**
     * 修改用户id
     */
    private Long updateUserId;
}
