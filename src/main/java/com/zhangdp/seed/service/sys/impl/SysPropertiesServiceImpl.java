package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.entity.sys.SysProperties;
import com.zhangdp.seed.mapper.sys.SysPropertiesMapper;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.service.sys.SysPropertiesService;
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
 * 2023/4/12 系统配置service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = TableNameConst.SYS_PROPERTIES)
public class SysPropertiesServiceImpl implements SysPropertiesService {

    private final SysPropertiesMapper sysPropertiesMapper;

    @Cacheable(key = "#code", unless = "#result == null")
    @Override
    public SysProperties getByCode(String code) {
        return sysPropertiesMapper.selectOneByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(SysProperties entity) {
        entity.setCode(entity.getCode().trim().toUpperCase());
        return sysPropertiesMapper.insert(entity) > 0;
    }

    @CacheEvict(key = "#param.code", condition = "#result == true")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysProperties entity) {
        SysProperties bean = this.sysPropertiesMapper.selectById(entity.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.PARAM_NOT_FOUND));
        SysProperties update = new SysProperties();
        update.setId(entity.getId());
        update.setTextValue(entity.getTextValue());
        update.setDescription(entity.getDescription());
        update.setIsEncrypted(entity.getIsEncrypted());
        // 只允许修改上面几项，其余字段不允许修改
        return sysPropertiesMapper.updateById(update) > 0;
    }

    @CacheEvict(allEntries = true, condition = "#result == true")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {
        SysProperties entity = this.sysPropertiesMapper.selectById(id);
        if (entity == null) {
            return true;
        }
        Assert.isFalse(entity.getIsSystem() == CommonConst.YES_TRUE, () -> new BizException(ErrorCode.SYSTEM_PARAM_CAN_NOT_DELETE));
        return sysPropertiesMapper.deleteById(id) > 0;
    }

    @Override
    public PageInfo<SysProperties> pageQuery(PageQuery<BaseQueryParams> pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getOrderBy());
        LambdaQueryWrapper<SysProperties> wrappers = Wrappers.lambdaQuery(SysProperties.class).orderByDesc(SysProperties::getId);
        BaseQueryParams param = pageQuery.getParams();
        if (param != null) {
            if (StrUtil.isNotBlank(param.getQuery())) {
                wrappers.like(SysProperties::getCode, param.getQuery())
                        .or().like(SysProperties::getDescription, param.getQuery());
            }
        }
        List<SysProperties> list = sysPropertiesMapper.selectList(wrappers);
        return new PageInfo<>(list);

    }
}
