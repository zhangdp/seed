package io.github.seed.common.component;

import tools.jackson.databind.ValueDeserializer;

/**
 * 2025/3/8 String类型自动trim() jackson反序列化器
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class StringTrimDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(tools.jackson.core.JsonParser p, tools.jackson.databind.DeserializationContext ctxt) throws tools.jackson.core.JacksonException {
        String s = p.getString();
        return s == null ? null : s.trim();
    }

    @Override
    public Class<?> handledType() {
        return String.class;
    }
}

