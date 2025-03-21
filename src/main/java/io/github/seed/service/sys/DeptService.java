package io.github.seed.service.sys;

import io.github.seed.entity.sys.Dept;
import io.github.seed.model.dto.DeptTreeNode;

import java.util.List;

/**
 * 2023/4/3 部门service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface DeptService {

    /**
     * 获取部门树
     *
     * @return
     */
    List<DeptTreeNode> listTree();

    /**
     * 根据判断部门是否存在
     *
     * @param id
     * @return
     */
    boolean exists(Long id);

    /**
     * 新增部门
     *
     * @param dept
     * @return
     */
    boolean insert(Dept dept);

    /**
     * 修改部门
     *
     * @param dept
     * @return
     */
    boolean update(Dept dept);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);
}
