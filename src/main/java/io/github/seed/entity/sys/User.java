package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.annotation.Desensitization;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.SensitiveType;
import io.github.seed.entity.LogicBaseEntity;
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
@Table(TableNameConst.SYS_USER)
@Schema(title = "用户")
public class User extends LogicBaseEntity<Long> implements Serializable {

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
    @Desensitization(SensitiveType.PASSWORD)
    private String password;
    /**
     * 手机号
     */
    @Schema(title = "手机号")
    @Desensitization(SensitiveType.MOBILE)
    private String mobile;
    /**
     * 性别，F：女，M：男，null：未知
     */
    @Schema(title = "性别", description = "F：女，M：男，null：未知")
    private Character gender;
    /**
     * 生日
     */
    @Schema(title = "生日")
    private LocalDate birthDate;
    /**
     * 邮箱
     */
    @Schema(title = "邮箱")
    @Desensitization(SensitiveType.EMAIL)
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
    // @Desensitization(SensitiveType.CHINESE_NAME)
    private String name;
    /**
     * 身份证号
     */
    @Schema(title = "身份证号")
    @Desensitization(SensitiveType.CITIZEN_ID)
    private String citizenId;
    /**
     * 部门id
     */
    @Schema(title = "部门id")
    private Long deptId;
    /**
     * 状态，0：正常，其它异常
     */
    @Schema(title = "状态", description = "0：正常；其它：锁定或者异常")
    private Integer status;

}
