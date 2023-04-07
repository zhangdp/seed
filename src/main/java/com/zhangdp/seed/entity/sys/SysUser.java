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
import java.time.LocalDate;

/**
 * 2023/4/3 用户表
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户信息详情")
public class SysUser extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    @Schema(title = "账号")
    private String username;
    /**
     * 密码
     */
    @Schema(title = "密码")
    private String password;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 性别，F：女，M：男，null：未知
     */
    @Schema(title = "性别", description = "F：女，M：男，null：未知")
    private Character sex;
    /**
     * 生日
     */
    private LocalDate birthDate;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像地址
     */
    private String avatarUrl;
    /**
     * 姓名
     */
    private String fullName;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 状态，0：正常，其它异常
     */
    private Integer status;

}
