package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典数据
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_DICT_DATA)
@Schema(description = "字典数据")
public class DictData extends BaseLogicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属字典id
     */
    @Schema(description = "字典id")
    private Long dictId;
    /**
     * 数据值
     */
    @Schema(description = "数据值")
    private String value;
    /**
     * 标签名
     */
    @Schema(description = "标签名")
    private String label;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
    /**
     * 扩展数据
     */
    @Schema(description = "扩展数据")
    private String metaData;
    /**
     * 排序（升序）
     */
    @Schema(description = "同级排序，升序")
    private Integer sorts;
}
