package io.github.seed.common.util;

import io.github.seed.model.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2023/5/27 树工具类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class TreeUtils {

    /**
     * 列表转树形结构
     *
     * @param list   列表
     * @param root   根
     * @param <T>    节点类型，需继承TreeNode
     * @param <K>    根id类型
     * @return
     */
    public static <T extends TreeNode<K>, K> List<T> listToTree(List<T> list, K root) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        Map<K, List<T>> map = list.stream().collect(Collectors.groupingBy(TreeNode::getParent));
        return findChildren(map, root);
    }

    /**
     * 递归查找孩子节点
     *
     * @param parentMap
     * @param parent
     * @param <T>
     * @param <K>
     * @return
     */
    private static <T extends TreeNode<K>, K> List<T> findChildren(Map<K, List<T>> parentMap, K parent) {
        List<T> list = parentMap.get(parent);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        // 循环递归寻找孩子节点
        for (T node : list) {
            List<T> tmp = findChildren(parentMap, node.getValue());
            node.setChildren(new ArrayList<>(tmp));
        }
        return list;
    }
}
