package io.github.seed.common.component;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 2025/12/18 超大数字安全序列化
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class SafeLongSerializer extends ValueSerializer<Long> {

    // 浏览器JavaScript数字最大范围-2^53 + 1 到 2^53 - 1，超过需要转为字符串
    private static final long MAX_SAFE_NUM = 9007199254740991L; // 2^53 - 1
    private static final long MIN_SAFE_NUM = -9007199254740991L;

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else if (value > MAX_SAFE_NUM || value < MIN_SAFE_NUM) {
            gen.writeString(value.toString());
            log.debug("数值{}超过JavaScript安全范围-2^53+1~2^53-1，JSON序列化时转为字符串", value);
        } else {
            gen.writeNumber(value);
        }
    }
}
