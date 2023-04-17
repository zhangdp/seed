package com.zhangdp.seed.mapper.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangdp.seed.entity.log.LogLogin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/17 登陆日志mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface LogLoginMapper extends BaseMapper<LogLogin> {
}
