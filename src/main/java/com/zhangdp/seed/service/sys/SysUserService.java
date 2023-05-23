package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.dto.UserInfo;
import org.dromara.hutool.crypto.digest.BCrypt;

/**
 * 2023/4/3 用户service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据账号查询
     *
     * @param username 账号
     * @return 用户
     */
    SysUser getByUsername(String username);

    /**
     * 是否已存在账号
     *
     * @param username
     * @return
     */
    boolean existsUsername(String username);

    /**
     * 新增
     *
     * @param user
     * @return
     */
    SysUser insert(UserInfo user);

    /**
     * 加密密码
     *
     * @param password
     * @return
     */
    default String encryptPassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 检测明文密码是否正确
     *
     * @param plainPassword
     * @param encryptedPassword
     * @return
     */
    default boolean checkPassword(String plainPassword, String encryptedPassword) {
        return BCrypt.checkpw(plainPassword, encryptedPassword);
    }
}
