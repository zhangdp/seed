package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.util.MybatisPlusHelper;
import io.github.seed.entity.sys.User;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * 统计指定账号的用户个数（包含已逻辑删除的）
     *
     * @param username
     * @return
     */
    @Select("SELECT COUNT(*) FROM " + TableNameConst.SYS_USER + " WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 统计指定账号但id不为指定id的用户个数（包含逻辑删除的）
     *
     * @param username
     * @param id
     * @return
     */
    @Select("SELECT COUNT(*) FROM " + TableNameConst.SYS_USER + " WHERE username = #{username} AND id != #{id}")
    int countByUsernameAndIdNot(@Param("username") String username, @Param("id") Long id);

    /**
     * 查询列表
     *
     * @param query
     * @return
     */
    List<UserInfo> queryList(IPage<UserInfo> page, @Param("param") UserQuery query);

    /**
     * 根据用户名查询单条记录
     *
     * @param username
     * @return
     */
    default User selectOneByUsername(String username) {
        return this.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUsername, username));
    }

    /**
     * 根据手机号查询单条记录
     *
     * @param mobile
     * @return
     */
    default User selectOneByMobile(String mobile) {
        return this.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getMobile, mobile));
    }

    /**
     * 根据手机号查询单条记录
     *
     * @param email
     * @return
     */
    default User selectOneByEmail(String email) {
        return this.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getEmail, email));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<UserInfo> selectPage(PageQuery<UserQuery> pageQuery) {
        Page<UserInfo> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        page.setOrders(MybatisPlusHelper.toOrderItems(pageQuery.getOrderBy(), "id"));
        List<UserInfo> list = this.queryList(page, pageQuery.getParams());
        return new PageData<>(list, page.getTotal(), page.getPages(), page.getSize());
    }

}
