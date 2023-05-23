package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.mapper.sys.SysUserMapper;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.service.sys.SysDeptService;
import com.zhangdp.seed.service.sys.SysUserService;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2023/4/3 用户service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysDeptService sysDeptService;

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username));
    }

    @Override
    public boolean existsUsername(String username) {
        return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser insert(UserInfo user) {
        SysUser bean = new SysUser();
        BeanUtil.copyProperties(user, bean, "id");
        Assert.isFalse(this.existsUsername(bean.getUsername()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + bean.getUsername() + "已存在"));
        if (bean.getDeptId() != null) {
            Assert.isTrue(sysDeptService.exists(bean.getDeptId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "部门" + (StrUtil.isNotBlank(user.getDeptName()) ? user.getDeptName() : "id" + bean.getDeptId()) + "不存在"));
        }
        if (StrUtil.isNotBlank(bean.getPassword())) {
            bean.setPassword(this.encryptPassword(bean.getPassword()));
        }
        this.save(bean);
        return bean;
    }

}
