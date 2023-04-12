package com.zhangdp.seed.common.component;

import cn.hutool.core.util.ReflectUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 事件处理器接口类
 *
 * @author zhangdp
 * @date 2022/12/2
 */
public interface IEventHandler {

    /**
     * 处理事件
     *
     * @param params
     * @param result
     * @param tag
     */
    void handler(Map<String, Object> params, Object result, String tag);

    /**
     * 是否执行
     *
     * @return
     */
    default boolean enable() {
        return true;
    }

    /**
     * 根据名称、类型获取参数
     *
     * @param params
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T obtainParam(Map<String, Object> params, String name, Class<T> clazz) {
        Object p = params.get(name);
        if (clazz.isInstance(p)) {
            return (T) p;
        } else {
            return null;
        }
    }

    /**
     * 获取指定类型的唯一参数，不唯一将会抛异常
     *
     * @param params
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T obtainParam(Map<String, Object> params, Class<T> clazz) {
        T ret = null;
        int size = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (clazz.isInstance(value)) {
                if (size == 0) {
                    ret = (T) value;
                } else {
                    throw new IllegalArgumentException("重复的" + clazz.getName() + "类型参数");
                }
                size++;
            }
        }
        return ret;
    }

    /**
     * 获取指定类型的所有参数
     *
     * @param params
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> Map<String, T> obtainParams(Map<String, Object> params, Class<T> clazz) {
        Map<String, T> ret = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (clazz.isInstance(value)) {
                ret.put(key, (T) value);
            }
        }
        return ret;
    }

    /**
     * 利用反射获取某个参数的某个属性值
     *
     * @param params
     * @param name
     * @param filed
     * @return
     */
    default Object invokeParamFiled(Map<String, Object> params, String name, String filed) {
        Object obj = params.get(name);
        if (obj == null) {
            return null;
        }
        try {
            return ReflectUtil.getFieldValue(obj, filed);
        } catch (Exception e) {
            return null;
        }
    }

}
