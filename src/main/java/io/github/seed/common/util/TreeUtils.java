package io.github.seed.common.util;

import io.github.seed.model.TreeNode;

import java.util.*;

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
     * @param list 平铺节点列表
     * @param root 根节点的父ID
     * @param <T>  节点 Value/ID 类型（如 Long, String）
     * @param <E>  节点自身的具体类型（需继承 TreeNode<T, E>）
     * @return 组装好的树形结构列表
     */
    public static <T, E extends TreeNode<T, E>> List<E> listToTree(List<E> list, T root) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 将所有节点存入 Map，方便通过 ID (value) 快速查找节点引用
        Map<T, E> nodeMap = new HashMap<>(list.size());
        for (E node : list) {
            // 初始化默认属性
            node.setIsLeaf(true);
            node.setChildren(Collections.emptyList());
            if (node.getValue() != null) {
                nodeMap.put(node.getValue(), node);
            }
        }

        List<E> resultTree = new ArrayList<>();

        // 2. 遍历列表，建立父子节点关系
        for (E node : list) {
            T parentId = node.getParent();
            // 判断是否为根节点（安全兼容 parentId 或 root 为 null 的情况）
            if (Objects.equals(parentId, root)) {
                resultTree.add(node);
            } else {
                // 查找当前节点的父节点
                E parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    // 若父节点的 children 为空，初始化 ArrayList
                    if (parentNode.getChildren() == null || parentNode.getChildren().isEmpty()) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    // 父节点加入当前节点
                    parentNode.getChildren().add(node);
                    // 父节点不再是叶子节点
                    parentNode.setIsLeaf(false);
                }
            }
        }

        return resultTree;
    }

}
