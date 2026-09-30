package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Dict;
import io.github.seed.mapper.sys.DictDataMapper;
import io.github.seed.mapper.sys.DictMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2023/4/12 字典service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;
    private final DictDataMapper dictDataMapper;

    @Override
    public Dict getByType(String type) {
        return dictMapper.selectOneByType(type);
    }

    @Override
    public PageData<Dict> queryPage(PageQuery<BaseTextQuery> pageQuery) {
        return this.dictMapper.queryPage(pageQuery);
    }

    @Override
    public List<Dict> listAll() {
        return this.dictMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Dict entity) {
        Assert.isFalse(dictMapper.existsByTypeAndIdNot(entity.getType(), null),
                () -> new BizException(ErrorCode.BIZ_ERROR.code(), "字典类型已存在"));
        return dictMapper.insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Dict entity) {
        Dict bean = this.dictMapper.selectOneById(entity.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.NOT_FOUND.code(), "字典不存在"));
        Assert.isFalse(dictMapper.existsByTypeAndIdNot(entity.getType(), entity.getId()),
                () -> new BizException(ErrorCode.BIZ_ERROR.code(), "字典类型已存在"));
        Dict update = new Dict();
        update.setId(entity.getId());
        update.setName(entity.getName());
        update.setDescription(entity.getDescription());
        // 类型与是否系统内置不允许修改，避免影响业务代码的字典取值
        return dictMapper.update(update) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Dict bean = this.dictMapper.selectOneById(id);
        if (bean == null) {
            return false;
        }
        Assert.isFalse(bean.getIsSystem() != null && bean.getIsSystem() == Const.YES_TRUE,
                () -> new BizException(ErrorCode.BIZ_ERROR.code(), "系统内置的字典不允许删除"));
        dictDataMapper.deleteByDictId(id);
        return dictMapper.deleteById(id) > 0;
    }
}
