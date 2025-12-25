package io.github.seed.common.component;

import cn.hutool.v7.core.text.StrUtil;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 2023/8/14 脱敏jackson序列化
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class DesensitizationJacksonSerializer extends ValueSerializer<String> {

    /**
     * 脱敏起点（含）
     */
    private final int start;
    /**
     * 脱敏终点（不含）
     */
    private final int end;
    /**
     * 脱敏替换字符，默认*
     */
    private final char mask;

    public DesensitizationJacksonSerializer(int start, int end) {
        this.start = start;
        this.end = end;
        this.mask = '*';
    }

    public DesensitizationJacksonSerializer(int start, int end, char mask) {
        this.start = start;
        this.end = end;
        this.mask = mask;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(StrUtil.replaceByCodePoint(value, start, end, mask));
    }

}
