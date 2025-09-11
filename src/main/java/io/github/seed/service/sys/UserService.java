package io.github.seed.service.sys;

import io.github.seed.entity.sys.User;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.AddUserDto;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;

/**
 * 2023/4/3 用户service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 根据账号查询
     *
     * @param username 账号
     * @return 用户
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询
     *
     * @param mobile
     * @return
     */
    User getByMobile(String mobile);

    /**
     * 根据邮箱查询
     *
     * @param email
     * @return
     */
    User getByEmail(String email);

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
    boolean add(AddUserDto user);

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    boolean update(AddUserDto user);

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
    PageData<UserInfo> queryPage(PageQuery<UserQuery> pageQuery);

}
