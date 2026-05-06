package io.github.seed.common.util;

import cn.hutool.v7.core.bean.BeanUtil;
import cn.hutool.v7.core.cache.Cache;
import cn.hutool.v7.core.cache.impl.FIFOCache;
import cn.hutool.v7.core.reflect.ClassUtil;
import cn.hutool.v7.core.reflect.FieldUtil;
import cn.hutool.v7.core.reflect.method.MethodUtil;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.annotation.Sensitive;
import io.github.seed.common.enums.SensitiveType;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 脱敏工具类
 *
 * @author zhangdp
 * @since 2026/4/27
 */
@Slf4j
public class DesensitizationUtil {

    /**
     * 字段元数据缓存
     */
    private static final Cache<Class<?>, List<FieldMeta>> CACHE = new FIFOCache<>(256);

    /**
     * 字段元数据
     *
     * @param field
     * @param getter
     * @param setter
     * @param sensitive
     */
    private record FieldMeta(Field field, Method getter, Method setter, Sensitive sensitive) {}

    /**
     * 获取某个类所有字段（包含父类的）元数据
     *
     * @param clazz
     * @return
     */
    private static List<FieldMeta> getSensitiveFieldMetas(Class<?> clazz) {
        List<FieldMeta> fieldMetas = CACHE.get(clazz);
        if (fieldMetas == null) {
            fieldMetas = new ArrayList<>();
            PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(clazz);
            if (descriptors != null) {
                for (PropertyDescriptor desc : descriptors) {
                    Method getter = desc.getReadMethod();
                    Method setter = desc.getWriteMethod();
                    if (getter == null || setter == null) {
                        continue;
                    }
                    Field field = FieldUtil.getField(clazz, desc.getName());
                    if (field == null) {
                        continue;
                    }
                    fieldMetas.add(new FieldMeta(field, getter, setter, field.getAnnotation(Sensitive.class)));
                }
            }
            CACHE.put(clazz, fieldMetas);
        }
        return fieldMetas;
    }


    /**
     * 将对象中的敏感字段（带有@Sensitive注解）进行脱敏
     *
     * @param obj
     */
    public static void desensitize(Object obj) {
        if (obj == null) {
            return;
        }
        desensitize(obj, new IdentityHashMap<>());
    }

    /**
     * 将对象中的敏感字段（带有@Sensitive注解）进行脱敏，增加map参数防止循环引用无限循环
     *
     * @param obj
     * @param visited
     */
    private static void desensitize(Object obj, IdentityHashMap<Object, Void> visited) {
        if (obj == null || visited.containsKey(obj)) {
            return;
        }
        visited.put(obj, null);

        Class<?> clazz = obj.getClass();
        // 简单类型无需处理
        if (ClassUtil.isSimpleValueType(clazz)) {
            return;
        }
        // 处理集合
        if (obj instanceof Collection<?> collection) {
            for (Object item : collection) {
                desensitize(item, visited);
            }
            return;
        }
        // 处理Map
        if (obj instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                desensitize(entry.getKey(), visited);
                desensitize(entry.getValue(), visited);
            }
            return;
        }
        // 处理数组
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                desensitize(Array.get(obj, i), visited);
            }
            return;
        }
        // 处理普通对象
        List<FieldMeta> fieldMetas = getSensitiveFieldMetas(clazz);
        if (fieldMetas == null || fieldMetas.isEmpty()) {
            return;
        }
        for (FieldMeta fieldMeta : fieldMetas) {
            Method setMethod = fieldMeta.setter;
            Method getMethod = fieldMeta.getter;
            Field field = fieldMeta.field;
            // 通过getter获取原来的值
            Object value = MethodUtil.invoke(obj, getMethod);
            if (value == null) {
                continue;
            }
            if (value instanceof String str) {
                Sensitive sensitive = fieldMeta.sensitive;
                if (sensitive != null) {
                    SensitiveType sensitiveType = sensitive.value();
                    String desensitized;
                    if (sensitiveType == SensitiveType.CUSTOM) {
                        desensitized = StrUtil.replaceByCodePoint(str, sensitive.start(), sensitive.end(), sensitive.mask());
                    } else {
                        desensitized = sensitiveType.getDesensitizer().apply(str);
                    }
                    // 将脱敏后的值通过setter方法重新赋值
                    MethodUtil.invoke(obj, setMethod, desensitized);
                    log.trace("{}的{}字段脱敏：sensitive: {}, 脱敏后值: {}", clazz.getName(), field.getName(), sensitive, desensitized);
                }
            } else {
                // 递归处理嵌套对象
                desensitize(value, visited);
            }
        }
    }

}
