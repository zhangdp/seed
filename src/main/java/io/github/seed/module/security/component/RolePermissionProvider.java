package io.github.seed.module.security.component;

import io.github.seed.module.security.data.RolePermissionGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * 角色对应的权限来源
 * <br>jwt里只带角色编码，权限由本接口在节点内存里展开，因此给角色加减权限可以即时生效，
 * 不必等令牌过期；接口定义在模块内，实现放在{@code manager}层，模块不反向依赖业务层
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface RolePermissionProvider {

    /**
     * 按角色编码取权限列表，含角色本身的授权标识
     *
     * @param roleCodes 角色编码列表，可为空
     * @return 授权列表，角色不存在时该项被忽略
     */
    List<RolePermissionGrantedAuthority> listAuthorities(Collection<String> roleCodes);

}
