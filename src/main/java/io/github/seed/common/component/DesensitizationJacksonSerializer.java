package io.github.seed.common.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;

import java.io.IOException;

/**
 * 2023/8/14 脱敏jackson序列化
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class DesensitizationJacksonSerializer extends JsonSerializer<String> {

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
    public void serialize(String str, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(StrUtil.replaceByCodePoint(str, start, end, mask));
    }

}
