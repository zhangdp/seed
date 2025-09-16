package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.User;
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
public interface UserMapper extends BaseMapper<User> {

    /**
     * 是否存在指定账号（包含已逻辑删除的）
     *
     * @param username
     * @return
     */
    default boolean existsByUsername(String username) {
        return LogicDeleteManager.execWithoutLogicDelete(() -> this.selectCountByQuery(QueryWrapper.create()
                .eq(User::getUsername, username)) > 0);
    }

    /**
     * 统计指定账号但id不为指定id的用户个数（包含逻辑删除的）
     *
     * @param username
     * @param id
     * @return
     */
    default boolean existsByUsernameAndIdNot(String username, Long id) {
        return LogicDeleteManager.execWithoutLogicDelete(() -> this.selectCountByQuery(QueryWrapper.create()
                .eq(User::getUsername, username).ne(User::getId, id)) > 0);
    }

    /**
     * 查询列表
     *
     * @param query
     * @return
     */
    default List<User> queryList(UserQuery query) {
        return this.selectListByQuery(QueryWrapper.create()
                .eq(User::getUsername, query.getUsername())
                .eq(User::getDeptId, query.getDeptId())
                .eq(User::getStatus, query.getStatus())
                .eq(User::getMobile, query.getMobile())
                .eq(User::getGender, query.getGender())
                .like(User::getName, query.getNameLike())
                .ne(User::getId, query.getLoginUserId(), query.isExcludeSelf())
        );
    }

    /**
     * 根据用户名查询单条记录
     *
     * @param username
     * @return
     */
    default User selectOneByUsername(String username) {
        return this.selectOneByQuery(QueryWrapper.create().eq(User::getUsername, username));
    }

    /**
     * 根据手机号查询单条记录
     *
     * @param mobile
     * @return
     */
    default User selectOneByMobile(String mobile) {
        return this.selectOneByQuery(QueryWrapper.create().eq(User::getMobile, mobile));
    }

    /**
     * 根据手机号查询单条记录
     *
     * @param email
     * @return
     */
    default User selectOneByEmail(String email) {
        return this.selectOneByQuery(QueryWrapper.create().eq(User::getEmail, email));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<UserInfo> selectPage(PageQuery<UserQuery> pageQuery) {
        UserQuery query = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create().orderBy(pageQuery.getOrderBy());
        if (query != null) {
            wrapper.eq(User::getUsername, query.getUsername())
                    .eq(User::getDeptId, query.getDeptId())
                    .eq(User::getStatus, query.getStatus())
                    .eq(User::getMobile, query.getMobile())
                    .eq(User::getGender, query.getGender())
                    .like(User::getName, query.getNameLike())
                    .ne(User::getId, query.getLoginUserId(), query.isExcludeSelf() && query.getLoginUserId() != null);
        }
        Page<UserInfo> page = this.paginateAs(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper, UserInfo.class);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }

}
