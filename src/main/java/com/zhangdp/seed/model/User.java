package com.zhangdp.seed.model;

import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 2023/4/4 用户信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors
@Schema(description = "用户信息")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id", description = "修改时需传")
    @NotNull(message = "id不能为空", groups = ValidGroup.Update.class)
    private Long id;
    /**
     * 账号
     */
    @Schema(title = "账号", description = "账号")
    private String username;
    /**
     * 手机号
     */
    @Schema(title = "手机号", description = "手机号")
    private String phone;
    /**
     * 性别，F：女，M：男，null：未知
     */
    @Schema(title = "性别", description = "F：女，M：男，null：未知")
    private Character sex;
    /**
     * 生日
     */
    @Schema(title = "生日")
    private LocalDate birthDate;
    /**
     * 邮箱
     */
    @Schema(title = "邮箱")
    private String email;
    /**
     * 头像地址
     */
    @Schema(title = "头像地址")
    private String avatarUrl;
    /**
     * 姓名
     */
    @Schema(title = "姓名")
    private String fullName;
    /**
     * 身份证号
     */
    @Schema(title = "身份证号")
    private String idCardNo;
    /**
     * 部门id
     */
    @Schema(title = "部门id")
    private Long deptId;
    /**
     * 创建时间
     */
    @Schema(title = "创建时间", description = "格式" + CommonConst.DATETIME_PATTERN)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @Schema(title = "修改时间", description = "格式" + CommonConst.DATETIME_PATTERN)
    private LocalDateTime updateTime;
}
