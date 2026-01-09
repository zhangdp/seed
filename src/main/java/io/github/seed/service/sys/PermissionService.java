package io.github.seed.service.sys;

import cn.hutool.v7.core.collection.CollUtil;
import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.PermissionType;
import io.github.seed.common.util.TreeUtils;
import io.github.seed.entity.sys.Permission;
import io.github.seed.model.dto.PermissionTreeNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface PermissionService {

    /**
     * 是否存在
     *
     * @param id
     * @return
     */
    boolean isExists(Long id);

    /**
     * 获取某个角色拥有的资源列表
     *
     * @param roleId
     * @return
     */
    List<Permission> listRoleResources(Long roleId);

    /**
     * 添加
     *
     * @param permission
     * @return
     */
    boolean add(Permission permission);

    /**
     * 修改
     *
     * @param permission
     * @return
     */
    boolean update(Permission permission);

    /**
     * 获取资源树列表
     *
     * @return
     */
    List<PermissionTreeNode> listTree();

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 根据角色id列表批量in查询
     *
     * @param roleIds
     * @return
     */
    List<Permission> listRoleResources(Collection<Long> roleIds);

    /**
     * 转为树形节点
     *
     * @param coll
     * @return
     */
    default List<PermissionTreeNode> toTree(Collection<Permission> coll) {
        if (CollUtil.isEmpty(coll)) {
            return Collections.emptyList();
        }
        List<PermissionTreeNode> treeList = coll.stream()
                .map(bean -> {
                    PermissionTreeNode n = new PermissionTreeNode();
                    n.setValue(bean.getId());
                    n.setParent(bean.getParentId());
                    n.setDescription(bean.getDescription());
                    n.setLabel(bean.getName());
                    n.setSorts(bean.getSorts());
                    n.setPermission(bean.getCode());
                    n.setIsVisible(bean.getVisible());
                    n.setIsKeepAlive(bean.getKeepAlive());
                    n.setIcon(bean.getIcon());
                    n.setPath(bean.getPath());
                    n.setDescription(bean.getDescription());
                    // 菜单不是叶子节点
                    n.setIsLeaf(PermissionType.MENU.type().equals(bean.getType()));
                    return n;
                })
                .sorted(Comparator.comparingInt(PermissionTreeNode::getSorts))
                .collect(Collectors.toList());
        return TreeUtils.listToTree(treeList, Const.ROOT_ID);
    }

}
