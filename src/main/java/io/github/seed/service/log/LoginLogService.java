package io.github.seed.service.log;

import io.github.seed.entity.log.LoginLog;
import io.github.seed.entity.log.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.PageQuery;
import jakarta.validation.Valid;

/**
 * 2023/4/17 登录日志service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface LoginLogService {

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(LoginLog entity);

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    PageData<LoginLog> queryPage(@Valid PageQuery<LoginLogQuery> pageQuery);
}
