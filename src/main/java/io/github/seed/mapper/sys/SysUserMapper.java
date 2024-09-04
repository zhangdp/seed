package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.seed.entity.sys.SysUser;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/3 用户mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据参数动态查询用户列表
     *
     * @param params
     * @return
     */
    List<UserInfo> queryList(UserQuery params);

    /**
     * 统计指定账号的用户个数（包含逻辑删除的）
     *
     * @param username
     * @return
     */
    int countByUsername(String username);

    /**
     * 统计指定账号但id不为指定id的用户个数（包含逻辑删除的）
     *
     * @param username
     * @param id
     * @return
     */
    int countByUsernameAndIdNot(String username, Long id);

    /**
     * 根据用户名查询单条记录
     *
     * @param username
     * @return
     */
    default SysUser selectOneByUsername(String username) {
        return this.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username));
    }

    /**
     * 根据手机号查询单条记录
     *
     * @param mobile
     * @return
     */
    default SysUser selectOneByMobile(String mobile) {
        return this.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getMobile, mobile));
    }

    /**
     * 根据手机号查询单条记录
     *
     * @param email
     * @return
     */
    default SysUser selectOneByEmail(String email) {
        return this.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getEmail, email));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<UserInfo> queryPage(PageQuery<UserQuery> pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getOrderBy());
        List<UserInfo> list = this.queryList(pageQuery.getParams());
        PageInfo<UserInfo> pi = new PageInfo<>(list);
        return new PageData<>(list, pi.getTotal(), pi.getPageNum(), pi.getPageSize());
    }

}
