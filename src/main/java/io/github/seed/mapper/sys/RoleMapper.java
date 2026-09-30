package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.UserRole;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/3 角色mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据批量角色id来in查询列表
     *
     * @param roleIds
     * @return
     */
    default List<Role> selectListByRoleIdIn(Collection<Long> roleIds) {
        return this.selectListByQuery(QueryWrapper.create().in(Role::getId, roleIds));
    }

    /**
     * 根据code查询单条记录
     *
     * @param code
     * @return
     */
    default Role selectOneByCode(String code) {
        return this.selectOneByQuery(QueryWrapper.create().eq(Role::getCode, code));
    }

    /**
     * 根据角色id查询是否存在
     *
     * @param roleId
     * @return
     */
    default boolean exists(Long roleId) {
        return this.selectCountByQuery(QueryWrapper.create().eq(Role::getId, roleId)) > 0;
    }

    /**
     * 获取用户具有的角色列表
     *
     * @param userId
     * @return
     */
    default List<Role> selectListByUserId(Long userId) {
        QueryWrapper subQuery = QueryWrapper.create().select(UserRole::getRoleId).from(UserRole.class).eq(UserRole::getUserId, userId);
        return this.selectListByQuery(QueryWrapper.create().in(Role::getId, subQuery));
    }

    /**
     * 统计指定标识但id不为指定id的角色个数（包含已逻辑删除的）
     *
     * @param code
     * @param id
     * @return
     */
    default boolean existsByCodeAndIdNot(String code, Long id) {
        return LogicDeleteManager.execWithoutLogicDelete(() -> this.selectCountByQuery(QueryWrapper.create()
                .eq(Role::getCode, code)
                .ne(Role::getId, id, id != null)) > 0);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<Role> queryPage(PageQuery<BaseTextQuery> pageQuery) {
        BaseTextQuery params = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create().orderBy(pageQuery.getOrderBy());
        if (params != null) {
            String query = params.getQuery();
            wrapper.and(w -> {
                w.like(Role::getName, query);
                w.or(Role::getCode).like(query);
            });
        }
        Page<Role> page = this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }

}
