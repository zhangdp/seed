package io.github.seed.common.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.github.seed.common.annotation.Desensitization;
import io.github.seed.common.enums.SensitiveType;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 2024/10/10 脱敏注解动态使用序列化器脱敏
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class DesensitizationJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    /**
     * 缓存每种类型的脱敏序列化器
     */
    private final Map<SensitiveType, JsonSerializer<String>> serializerCache = new HashMap<>();

    public DesensitizationJacksonAnnotationIntrospector() {
        for (SensitiveType type : SensitiveType.values()) {
            if (type != SensitiveType.CUSTOMER) {
                serializerCache.put(type, new JsonSerializer<>() {
                    @Override
                    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        gen.writeString(type.getDesensitizer().apply(value));
                    }
                });
            }
        }
    }

    /**
     * 初始化
     */
    public void init() {
        for (SensitiveType type : SensitiveType.values()) {
            if (type != SensitiveType.CUSTOMER) {
                serializerCache.put(type, new JsonSerializer<>() {
                    @Override
                    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        gen.writeString(type.getDesensitizer().apply(value));
                    }
                });
            }
        }
    }

    @Override
    public Object findSerializer(Annotated a) {
        if (a.getRawType().equals(String.class)) {
            // 如果含有需要脱敏的@Desensitization注解，则根据上下文动态创建序列化器
            Desensitization desensitization = a.getAnnotation(Desensitization.class);
            if (desensitization != null) {
                SensitiveType type = desensitization.value();
                if (type != SensitiveType.CUSTOMER) {
                    JsonSerializer<String> serializer = serializerCache.get(type);
                    Assert.notNull(serializer, "No JsonSerializer for " + type);
                    log.debug("字段{}使用脱敏序列化，脱敏类型: {}，脱敏序列化器：{}", a.getName(), type, serializer);
                    return serializer;
                } else {
                    log.debug("字段{}使用自定义脱敏序列化，脱敏起始位置: {}，脱敏截止位置: {}，遮罩字符: {}", a.getName(), desensitization.start(), desensitization.end(), desensitization.mask());
                    return new DesensitizationJacksonSerializer(desensitization.start(), desensitization.end(), desensitization.mask());
                }
            }
        }
        return super.findSerializer(a);
    }
}
