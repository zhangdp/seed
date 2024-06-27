package com.zhangdp.seed.service.sys;

import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.PageData;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.model.params.UserQuery;

/**
 * 2023/4/3 用户service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysUserService {

    /**
     * 根据账号查询
     *
     * @param username 账号
     * @return 用户
     */
    SysUser getByUsername(String username);

    /**
     * 根据手机号查询
     *
     * @param mobile
     * @return
     */
    SysUser getByMobile(String mobile);

    /**
     * 根据邮箱查询
     *
     * @param email
     * @return
     */
    SysUser getByEmail(String email);

    /**
     * 是否已存在账号
     *
     * @param username
     * @return
     */
    boolean existsUsername(String username);

    /**
     * 是否已存在账号且id不等于
     *
     * @param username
     * @param id
     * @return
     */
    boolean existsUsernameAndIdNot(String username, Long id);

    /**
     * 新增
     *
     * @param user
     * @return
     */
    boolean insert(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    boolean update(SysUser user);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    PageData<UserInfo> pageQuery(PageQuery<UserQuery> pageQuery);

}
