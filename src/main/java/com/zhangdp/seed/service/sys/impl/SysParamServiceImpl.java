package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.mapper.sys.SysParamMapper;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.service.sys.SysParamService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = TableNameConst.SYS_PARAM)
public class SysParamServiceImpl implements SysParamService {

    private final SysParamMapper sysParamMapper;

    @Cacheable(key = "#code", unless = "#result == null")
    @Override
    public SysParam getByCode(String code) {
        return sysParamMapper.selectOneByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(SysParam param) {
        param.setCode(param.getCode().trim().toUpperCase());
        return sysParamMapper.insert(param) > 0;
    }

    @CacheEvict(key = "#param.code", condition = "#result == true")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysParam param) {
        SysParam bean = this.sysParamMapper.selectById(param.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.PARAM_NOT_FOUND));
        SysParam update = new SysParam();
        update.setId(param.getId());
        update.setParamValue(param.getParamValue());
        update.setDescription(param.getDescription());
        update.setIsEncrypted(param.getIsEncrypted());
        // 只允许修改上面几项，其余字段不允许修改
        return sysParamMapper.updateById(update) > 0;
    }

    @CacheEvict(allEntries = true, condition = "#result == true")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {
        SysParam param = this.sysParamMapper.selectById(id);
        if (param == null) {
            return true;
        }
        Assert.isFalse(param.getIsSystem() == CommonConst.YES_TRUE, () -> new BizException(ErrorCode.SYSTEM_PARAM_CAN_NOT_DELETE));
        return sysParamMapper.deleteById(id) > 0;
    }

    @Override
    public PageInfo<SysParam> pageQuery(PageQuery<BaseQueryParams> pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getOrderBy());
        LambdaQueryWrapper<SysParam> wrappers = Wrappers.lambdaQuery(SysParam.class).orderByDesc(SysParam::getId);
        BaseQueryParams param = pageQuery.getParams();
        if (param != null) {
            if (StrUtil.isNotBlank(param.getQuery())) {
                wrappers.like(SysParam::getCode, param.getQuery())
                        .or().like(SysParam::getDescription, param.getQuery());
            }
        }
        List<SysParam> list = sysParamMapper.selectList(wrappers);
        return new PageInfo<>(list);

    }
}
