package com.zhangdp.seed.common.util;

import com.zhangdp.seed.model.TreeNode;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.collection.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2023/5/27 树工具类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class TreeUtil {

    /**
     * 列表转树形结构
     *
     * @param list
     * @param rootId
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T extends TreeNode<K>, K> List<T> listToTree(List<T> list, K rootId) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>(0);
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
        if (!list.isEmpty()) {
            // 循环递归寻找孩子节点
            for (T node : list) {
                List<T> tmp = findChildren(map, node.getId());
                // T不匹配TreeNode<K>，无法直接set列表，只能一个个添加
                node.setChilds(new ArrayList<>(CollUtil.size(tmp)));
                if (CollUtil.isNotEmpty(tmp)) {
                    for (T t : tmp) {
                        node.getChilds().add(t);
                    }
                }
            }
        }
        return list;
    }
}
