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
 * 2023/4/12 字典数据
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_dict_data")
@Schema(description = "字典数据")
public class SysDictData extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属字典id
     */
    @Schema(title = "字典id")
    private Long dictId;
    /**
     * 数据值
     */
    @Schema(title = "数据值")
    private String value;
    /**
     * 标签名
     */
    @Schema(title = "标签名")
    private String label;
    /**
     * 描述
     */
    @Schema(title = "描述")
    private String description;
    /**
     * 排序（升序）
     */
    @Schema(title = "排序", description = "同级排序，升序")
    private Integer sorts;
}
