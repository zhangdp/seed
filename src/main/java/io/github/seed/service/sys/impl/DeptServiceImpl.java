package io.github.seed.service.sys.impl;

import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.util.TreeUtils;
import io.github.seed.entity.BaseEntity;
import io.github.seed.entity.sys.Dept;
import io.github.seed.mapper.sys.DeptMapper;
import io.github.seed.model.dto.DeptTreeNode;
import io.github.seed.service.sys.DeptService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2023/4/3 部门service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<DeptTreeNode> listTree() {
        List<Dept> list = deptMapper.selectAll();
        if (list == null) {
            return Collections.emptyList();
        }
        List<DeptTreeNode> treeList = list.stream().map(d -> {
            DeptTreeNode tn = new DeptTreeNode();
            tn.setValue(d.getId());
            tn.setParent(d.getParentId());
            tn.setLabel(d.getName());
            tn.setSorts(d.getSorts());
            return tn;
        }).sorted(Comparator.comparingInt(DeptTreeNode::getSorts)).collect(Collectors.toList());
        return TreeUtils.listToTree(treeList, Const.ROOT_ID);
    }

    @Override
    public boolean exists(Long id) {
        return deptMapper.existsById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(Dept dept) {
        if (!dept.getParentId().equals(Const.ROOT_ID)) {
            Assert.isTrue(this.exists(dept.getParentId()), () -> new BizException(ErrorCode.DEPT_PARENT_NOT_EXISTS.code(), "父部门（id=" + dept.getParentId() + "）已不存在"));
        }
        return deptMapper.insert(dept) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Dept dept) {
        Assert.isFalse(this.exists(dept.getId()), () -> new BizException(ErrorCode.DEPT_NOT_EXISTS.code(), "部门（id=" + dept.getId() + "）已不存在"));
        Dept bean = new Dept();
        BeanUtil.copyProperties(dept, bean, BaseEntity.CREATED_DATE, BaseEntity.LAST_MODIFIED_DATE);
        if (bean.getParentId() != null && !bean.getParentId().equals(Const.ROOT_ID)) {
            Assert.isTrue(this.exists(dept.getId()), () -> new BizException(ErrorCode.DEPT_PARENT_NOT_EXISTS.code(), "父部门（id=" + dept.getParentId() + "）已不存在"));
        }
        return deptMapper.updateById(bean) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return deptMapper.deleteById(id) > 0;
    }
}
