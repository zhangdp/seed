package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/6/1 查询用户
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Schema(title = "查询用户")
public class UserQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    @Schema(title = "账号", description = "不区分大小写完全匹配")
    private String username;
    /**
     * 手机号
     */
    @Schema(title = "手机号", description = "完全匹配")
    private String mobile;
    /**
     * 部门id
     */
    @Schema(title = "部门id", description = "递归匹配")
    private Long deptId;
    /**
     * 姓名模糊
     */
    @Schema(title = "姓名", description = "模糊匹配")
    private String nameLike;
    /**
     * 状态
     */
    @Schema(title = "状态")
    private Integer status;
    /**
     * 状态
     */
    @Schema(title = "性别")
    private Character gender;
    /**
     * 当前登录用户id
     */
    @Schema(title = "当前登录用户id", hidden = true)
    private Long loginUserId;
    /**
     * 是否排除自己
     */
    @Schema(title = "是否排除自己", description = "查询结果是否忽略自己")
    private Boolean excludeSelf;
}
