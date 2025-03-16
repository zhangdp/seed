package io.github.seed.common.component;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 2025/3/8 String类型自动trim() jackson反序列化器
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class StringTrimDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String s = jsonParser.getText();
        return s != null ? s.trim() : null;
    }

    @Override
    public Class<?> handledType() {
        return String.class;
    }
}

