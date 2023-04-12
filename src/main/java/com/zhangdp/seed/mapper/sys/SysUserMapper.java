package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangdp.seed.entity.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/3 用户mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
