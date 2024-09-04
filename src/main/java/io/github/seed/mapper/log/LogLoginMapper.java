package io.github.seed.mapper.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.seed.entity.log.LogLogin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/17 登录日志mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface LogLoginMapper extends BaseMapper<LogLogin> {
}
