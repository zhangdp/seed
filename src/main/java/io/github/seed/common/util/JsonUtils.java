package io.github.seed.common.util;

import cn.hutool.v7.core.lang.Assert;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * 2024/12/31 json工具类。使用spring自带的jackson
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class JsonUtils {

    /**
     * 空json
     */
    public static final String EMPTY = "{}";

    private static volatile JsonMapper MAPPER;

    /**
     * 对象转json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return MAPPER.writeValueAsString(object);
    }

    /**
     * json字符串转bean
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return MAPPER.readValue(json, clazz);
    }

    /**
     * 格式化json字符串
     *
     * @param json
     * @param isPretty
     * @return
     */
    public static String formatJson(String json, boolean isPretty) {
        Object jsonObject = MAPPER.readTree(json);
        return isPretty ? MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject) : MAPPER.writeValueAsString(jsonObject);
    }

    /**
     * 是否是json字符串
     *
     * @param json
     * @return
     */
    public static boolean isJson(String json) {
        try {
            // 尝试解析 JSON 字符串
            MAPPER.readTree(json);
            return true; // 如果没有抛出异常，说明是合法的 JSON
        } catch (JacksonException e) {
            return false; // 解析失败，说明不是合法的 JSON
        }
    }

    /**
     * 初始化
     *
     * @param jsonMapper
     */
    public static void init(JsonMapper jsonMapper) {
        Assert.notNull(jsonMapper);
        MAPPER = jsonMapper;
    }
}
