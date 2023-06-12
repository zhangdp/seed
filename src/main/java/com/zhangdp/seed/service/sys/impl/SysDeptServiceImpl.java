package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.SeedException;
import com.zhangdp.seed.common.util.TreeUtil;
import com.zhangdp.seed.entity.sys.SysDept;
import com.zhangdp.seed.mapper.sys.SysDeptMapper;
import com.zhangdp.seed.model.dto.DeptTreeNode;
import com.zhangdp.seed.service.sys.SysDeptService;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<DeptTreeNode> listTree() {
        List<SysDept> list = this.list();
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>(0);
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
        return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysDept::getId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(SysDept dept) {
        if (!dept.getParentId().equals(CommonConst.ROOT_ID)) {
            Assert.isTrue(this.exists(dept.getParentId()), () -> new SeedException(ErrorCode.DEPT_PARENT_NOT_EXISTS.code(), "父部门（id=" + dept.getParentId() + "）已不存在"));
        }
        return this.save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysDept dept) {
        Assert.isFalse(this.exists(dept.getId()), () -> new SeedException(ErrorCode.DEPT_NOT_EXISTS.code(), "部门（id=" + dept.getId() + "）已不存在"));
        SysDept bean = new SysDept();
        BeanUtil.copyProperties(dept, bean, "createTime", "updateTime");
        if (bean.getParentId() != null && !bean.getParentId().equals(CommonConst.ROOT_ID)) {
            Assert.isTrue(this.exists(dept.getId()), () -> new SeedException(ErrorCode.DEPT_PARENT_NOT_EXISTS.code(), "父部门（id=" + dept.getParentId() + "）已不存在"));
        }
        return this.updateById(bean);
    }
}
