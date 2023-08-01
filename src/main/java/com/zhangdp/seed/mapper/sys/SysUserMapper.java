package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.dto.UserInfo;
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
     * 查询用户列表
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
}
