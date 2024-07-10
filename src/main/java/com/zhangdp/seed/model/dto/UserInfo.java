package com.zhangdp.seed.model.dto;

import com.zhangdp.seed.common.annotation.Desensitization;
import com.zhangdp.seed.common.constant.Const;
import com.zhangdp.seed.common.enums.DesensitizationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.hutool.core.regex.RegexPool;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 2023/5/17 用户信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Schema(title = "用户信息")
public class UserInfo implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @Schema(title = "用户id", description = "修改时需传")
    private Long id;
    /**
     * 创建时间
     */
    @Schema(title = "创建时间", description = "格式：" + Const.DATETIME_FORMATTER)
    private LocalDateTime createdDate;
    /**
     * 修改时间
     */
    @Schema(title = "修改时间", description = "格式：" + Const.DATETIME_FORMATTER)
    private LocalDateTime modifiedDate;
    /**
     * 账号
     */
    @Schema(title = "账号")
    @NotBlank(message = "账号不能为空")
    @Length(min = 6, max = 20, message = "账号需为6到20个字符")
    @Pattern(regexp = RegexPool.GENERAL, message = "账号只能由字符英文字母 、数字和下划线构成")
    private String username;
    /**
     * 密码
     */
    @Schema(title = "密码")
    @NotBlank(message = "密码不能为空")
    @Length(min = 8, max = 32, message = "密码最少8位、最多32位字符")
    @Desensitization(DesensitizationType.PASSWORD)
    private String password;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegexPool.MOBILE, message = "手机号格式不正确")
    // @Desensitization(DesensitizationType.MOBILE)
    private String mobile;
    /**
     * 性别，F：女，M：男，null：未知
     */
    @Schema(title = "性别", description = "F：女，M：男，null：未知")
    private Character gender;
    /**
     * 生日
     */
    @Schema(title = "生日", description = "格式：" + Const.DATETIME_FORMATTER)
    @PastOrPresent(message = "生日需为过去日期")
    private LocalDate birth;
    /**
     * 邮箱
     */
    @Schema(title = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
    /**
     * 头像url地址
     */
    @Schema(title = "头像url地址")
    @Email(message = "头像url地址格式不正确")
    private String avatarUrl;
    /**
     * 姓名
     */
    @Schema(title = "姓名")
    @Pattern(regexp = RegexPool.CHINESE_NAME, message = "姓名格式不正确")
    private String name;
    /**
     * 住址
     */
    @Schema(title = "住址")
    @Length(max = 255, message = "住址最多255个字符")
    private String address;
    /**
     * 身份证号
     */
    @Schema(title = "身份证号")
    @Pattern(regexp = RegexPool.CITIZEN_ID, message = "身份证号码不正确")
    // @Desensitization(DesensitizationType.CITIZEN_ID)
    private String citizenId;
    /**
     * 部门id
     */
    @Schema(title = "部门id")
    private Long deptId;
    /**
     * 状态，0：正常，其它异常
     */
    @Schema(title = "状态", description = "0：正常；其它：锁定异常")
    private Integer status;
    /**
     * 部门名称
     */
    @Schema(title = "部门")
    private String deptName;
    /**
     * 角色列表
     */
    @Schema(title = "角色列表")
    private List<String> roles;
    /**
     * 权限列表
     */
    @Schema(title = "权限列表")
    private List<String> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        if (permissions != null && !permissions.isEmpty()) {
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == Const.GOOD;
    }
}
