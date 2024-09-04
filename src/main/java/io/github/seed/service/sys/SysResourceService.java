package io.github.seed.service.sys;

import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.ResourceType;
import io.github.seed.util.TreeUtils;
import io.github.seed.entity.sys.SysResource;
import io.github.seed.model.dto.ResourceTreeNode;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2023/4/12 资源service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysResourceService {

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
    List<SysResource> listRoleResources(Long roleId);

    /**
     * 添加
     *
     * @param resource
     * @return
     */
    boolean add(SysResource resource);

    /**
     * 修改
     *
     * @param resource
     * @return
     */
    boolean update(SysResource resource);

    /**
     * 获取资源数列表
     *
     * @return
     */
    List<ResourceTreeNode> listTree();

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 转为树形节点
     *
     * @param coll
     * @return
     */
    default List<ResourceTreeNode> toTree(Collection<SysResource> coll) {
        if (CollUtil.isEmpty(coll)) {
            return new ArrayList<>(0);
        }
        List<ResourceTreeNode> treeList = coll.stream()
                .map(bean -> {
                    ResourceTreeNode n = new ResourceTreeNode();
                    n.setId(bean.getId());
                    n.setParentId(bean.getParentId());
                    n.setLabel(bean.getName());
                    n.setSorts(bean.getSorts());
                    n.setPermission(bean.getPermission());
                    n.setIsVisible(bean.getIsVisible());
                    n.setIsKeepAlive(bean.getIsKeepAlive());
                    n.setIcon(bean.getIcon());
                    n.setPath(bean.getPath());
                    n.setDescription(bean.getDescription());
                    // 菜单不是叶子节点
                    n.setIsLeaf(bean.getType() != ResourceType.MENU.type());
                    return n;
                })
                .sorted(Comparator.comparingInt(ResourceTreeNode::getSorts))
                .collect(Collectors.toList());
        return TreeUtils.listToTree(treeList, Const.ROOT_ID);
    }

}
