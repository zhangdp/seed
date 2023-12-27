package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.SeedException;
import com.zhangdp.seed.common.util.TreeUtil;
import com.zhangdp.seed.entity.BaseEntity;
import com.zhangdp.seed.entity.sys.SysDept;
import com.zhangdp.seed.mapper.sys.SysDeptMapper;
import com.zhangdp.seed.model.dto.DeptTreeNode;
import com.zhangdp.seed.service.sys.SysDeptService;
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
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<DeptTreeNode> listTree() {
        List<SysDept> list = sysDeptMapper.selectAll();
        if (list == null) {
            return Collections.emptyList();
        }
        List<DeptTreeNode> treeList = list.stream().map(d -> {
            DeptTreeNode tn = new DeptTreeNode();
            tn.setId(d.getId());
            tn.setParentId(d.getParentId());
            tn.setLabel(d.getName());
            tn.setSorts(d.getSorts());
            return tn;
        }).sorted(Comparator.comparingInt(DeptTreeNode::getSorts)).collect(Collectors.toList());
        return TreeUtil.listToTree(treeList, CommonConst.ROOT_ID);
    }

    @Override
    public boolean exists(Long id) {
        return sysDeptMapper.existsById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(SysDept dept) {
        if (!dept.getParentId().equals(CommonConst.ROOT_ID)) {
            Assert.isTrue(this.exists(dept.getParentId()), () -> new SeedException(ErrorCode.DEPT_PARENT_NOT_EXISTS.code(), "父部门（id=" + dept.getParentId() + "）已不存在"));
        }
        return sysDeptMapper.insert(dept) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysDept dept) {
        Assert.isFalse(this.exists(dept.getId()), () -> new SeedException(ErrorCode.DEPT_NOT_EXISTS.code(), "部门（id=" + dept.getId() + "）已不存在"));
        SysDept bean = new SysDept();
        BeanUtil.copyProperties(dept, bean, BaseEntity.CREATED_AT, BaseEntity.MODIFIED_AT);
        if (bean.getParentId() != null && !bean.getParentId().equals(CommonConst.ROOT_ID)) {
            Assert.isTrue(this.exists(dept.getId()), () -> new SeedException(ErrorCode.DEPT_PARENT_NOT_EXISTS.code(), "父部门（id=" + dept.getParentId() + "）已不存在"));
        }
        return sysDeptMapper.updateById(bean) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return sysDeptMapper.deleteById(id) > 0;
    }
}
