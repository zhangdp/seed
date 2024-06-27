package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.mapper.sys.SysParamMapper;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.service.sys.SysParamService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SysParamServiceImpl implements SysParamService {

    private final SysParamMapper sysParamMapper;

    @Override
    public SysParam getByCode(String code) {
        return sysParamMapper.selectOneByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(SysParam param) {
        return sysParamMapper.insert(param) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysParam param) {
        // SysParam bean = this.getById(param.getId());
        // Assert.notNull(bean, () -> new SeedException(ErrorCode.PARAM_NOT_FOUND));
        return sysParamMapper.updateById(param) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {
        return sysParamMapper.deleteById(id) > 0;
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
        List<SysParam> list = sysParamMapper.selectList(wrappers);
        return new PageInfo<>(list);
    }
}
