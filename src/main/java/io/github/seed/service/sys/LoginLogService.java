package io.github.seed.service.sys;

import io.github.seed.entity.sys.LoginLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.PageQuery;

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
    PageData<LoginLog> queryPage(PageQuery<LoginLogQuery> pageQuery);
}
