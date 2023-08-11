package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.SeedException;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.mapper.sys.SysParamMapper;
import com.zhangdp.seed.model.query.BaseQueryParams;
import com.zhangdp.seed.model.query.PageQuery;
import com.zhangdp.seed.service.sys.SysParamService;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2023/4/12 系统参数service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements SysParamService {

    @Override
    public SysParam getByCode(String code) {
        return this.getOne(Wrappers.lambdaQuery(SysParam.class).eq(SysParam::getCode, code));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insert(SysParam param) {
        return this.save(param);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysParam param) {
        SysParam bean = this.getById(param.getId());
        Assert.notNull(bean, () -> new SeedException(ErrorCode.PARAM_NOT_FOUND));
        return this.updateById(param);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }

    @Override
    public PageInfo<SysParam> pageQuery(PageQuery<BaseQueryParams> pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getOrderBy());
        LambdaQueryWrapper<SysParam> wrappers = Wrappers.lambdaQuery(SysParam.class);
        BaseQueryParams param = pageQuery.getParams();
        if (param != null) {
            if (StrUtil.isNotBlank(param.getQuery())) {
                wrappers.like(SysParam::getCode, param.getQuery());
            }
        }
        List<SysParam> list = this.list(wrappers);
        return new PageInfo<>(list);
    }
}
