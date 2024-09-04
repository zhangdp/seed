package io.github.seed.util;

import io.github.seed.model.TreeNode;
import org.dromara.hutool.core.collection.ListUtil;

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
     * @param rootId 根id
     * @param <T>    节点类型，需继承TreeNode
     * @param <K>    根id类型
     * @return
     */
    public static <T extends TreeNode<K>, K> List<T> listToTree(List<T> list, K rootId) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        Map<K, List<T>> map = list.stream().collect(Collectors.groupingBy(TreeNode::getParentId));
        return findChildren(map, rootId);
    }

    /**
     * 递归查找孩子节点
     *
     * @param map
     * @param rootId
     * @param <T>
     * @param <K>
     * @return
     */
    private static <T extends TreeNode<K>, K> List<T> findChildren(Map<K, List<T>> map, K rootId) {
        List<T> list = map.getOrDefault(rootId, ListUtil.empty());
        if (list != null && !list.isEmpty()) {
            // 循环递归寻找孩子节点
            for (T node : list) {
                List<T> tmp = findChildren(map, node.getId());
                int size = tmp.size();
                // T不匹配TreeNode<K>，无法直接set列表，只能一个个添加
                List<TreeNode<K>> children = new ArrayList<>(size);
                node.setChildren(children);
                if (size > 0) {
                    children.addAll(tmp);
                }
            }
        }
        return list;
    }
}
