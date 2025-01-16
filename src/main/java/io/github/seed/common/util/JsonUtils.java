package io.github.seed.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Getter;

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
     * 获取objectMapper对象
     *
     * @return
     */
    @Getter
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();

        // 设置Date格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 反序列化时未知字段报错：false
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        JavaTimeModule timeModule = new JavaTimeModule();
        // ======================= 时间序列化规则 ==============================
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMATTER)));
        timeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        timeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMATTER)));

        // ======================= 时间反序列化规则 ==============================
        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        timeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        timeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMATTER)));
        objectMapper.registerModule(timeModule);
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
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("对象序列化成JSON字符串失败", e);
        }
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
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON字符串反序列化成对象失败", e);
        }
    }

    /**
     * 格式化json字符串
     *
     * @param json
     * @param isPretty
     * @return
     */
    public static String formatJson(String json, boolean isPretty) {
        try {
            Object jsonObject = objectMapper.readTree(json);
            return isPretty ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject) : objectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error formatting JSON string", e);
        }
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
            objectMapper.readTree(json);
            return true; // 如果没有抛出异常，说明是合法的 JSON
        } catch (JsonProcessingException e) {
            return false; // 解析失败，说明不是合法的 JSON
        }
    }

}
