package io.github.seed.common.component;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.annotation.Desensitization;
import io.github.seed.common.enums.SensitiveType;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

/**
 * 2024/10/10 是否使用脱敏序列化拦截器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class DesensitizationJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 缓存每种类型的脱敏序列化器
     */
    private final Map<SensitiveType, ValueSerializer<String>> serializerCache = new HashMap<>();

    public DesensitizationJacksonAnnotationIntrospector() {
        for (SensitiveType type : SensitiveType.values()) {
            if (type != SensitiveType.CUSTOMER) {
                serializerCache.put(type, new ValueSerializer<>() {
                    @Override
                    public void serialize(String value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                        gen.writeString(type.getDesensitizer().apply(value));
                    }
                });
            }
        }
    }

    @Override
    public Object findSerializer(MapperConfig<?> config, Annotated a) {
        if (a.getRawType().equals(String.class)) {
            // 如果含有需要脱敏的@Desensitization注解，则根据上下文动态创建序列化器
            Desensitization desensitization = a.getAnnotation(Desensitization.class);
            if (desensitization != null) {
                SensitiveType type = desensitization.value();
                if (type == SensitiveType.CUSTOMER) {
                    log.trace("字段{}使用自定义脱敏序列化，脱敏起始位置: {}，脱敏截止位置: {}，遮罩字符: {}", a.getName(), desensitization.start(), desensitization.end(), desensitization.mask());
                    return new DesensitizationJacksonSerializer(desensitization.start(), desensitization.end(), desensitization.mask());
                } else {
                    ValueSerializer<String> serializer = serializerCache.get(type);
                    Assert.notNull(serializer, "No JsonSerializer for " + type);
                    log.trace("字段{}使用脱敏序列化，脱敏类型: {}，脱敏序列化器：{}", a.getName(), type, serializer);
                    return serializer;
                }
            }
        }
        return super.findSerializer(config, a);
    }
}
