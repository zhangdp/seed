package io.github.seed.common.util;

import lombok.Getter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    /**
     * 时间格式模块
     */
    public static final SimpleModule TIME_MODULE;

    /**
     * 日期格式化
     */
    public static final String DATE_FORMATTER = "yyyy-MM-dd";
    /**
     * 时间格式化，注意[.SSS]可选的毫秒值
     */
    public static final String TIME_FORMATTER = "HH:mm:ss[.SSS]";
    /**
     * 日期时间默认格式化
     */
    public static final String DATETIME_FORMATTER = DATE_FORMATTER + " " + TIME_FORMATTER;
    /**
     * jackson
     * -- GETTER --
     * 获取JsonMapper对象
     *
     * @return
     */
    @Getter
    private static final JsonMapper jsonMapper;

    static {
        TIME_MODULE = new SimpleModule();
        // ======================= 时间序列化规则 ==============================
        TIME_MODULE.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMATTER)));
        TIME_MODULE.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        TIME_MODULE.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMATTER)));

        // ======================= 时间反序列化规则 ==============================
        TIME_MODULE.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMATTER)));
        TIME_MODULE.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        TIME_MODULE.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMATTER)));

        jsonMapper = JsonMapper.builder()
                // .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .addModule(TIME_MODULE)
                .build();
    }

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
        return jsonMapper.writeValueAsString(object);
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
        return jsonMapper.readValue(json, clazz);
    }

    /**
     * 格式化json字符串
     *
     * @param json
     * @param isPretty
     * @return
     */
    public static String formatJson(String json, boolean isPretty) {
        Object jsonObject = jsonMapper.readTree(json);
        return isPretty ? jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject) : jsonMapper.writeValueAsString(jsonObject);
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
            jsonMapper.readTree(json);
            return true; // 如果没有抛出异常，说明是合法的 JSON
        } catch (JacksonException e) {
            return false; // 解析失败，说明不是合法的 JSON
        }
    }

}
