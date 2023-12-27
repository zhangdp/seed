package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.PageData;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.model.query.PageQuery;
import com.zhangdp.seed.model.query.UserQuery;
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
     * 根据用户名获取单条记录
     *
     * @param username
     * @return
     */
    default SysUser selectOneByUsername(String username) {
        return this.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<UserInfo> pageQuery(PageQuery<UserQuery> pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getOrderBy());
        List<UserInfo> list = this.queryList(pageQuery.getParams());
        PageInfo<UserInfo> pi = new PageInfo<>(list);
        return new PageData<>(list, pi.getTotal(), pi.getPageNum(), pi.getPageSize());
    }
}
