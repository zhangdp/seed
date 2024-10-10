package io.github.seed.common.component;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.github.seed.common.annotation.Desensitization;
import lombok.extern.slf4j.Slf4j;

/**
 * 2024/10/10 脱敏注解动态使用序列化器脱敏
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class DesensitizationJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Object findSerializer(Annotated a) {
        if (a.getRawType().equals(String.class)) {
            // 如果含有需要脱敏的@Desensitization注解，则根据上下文动态创建序列化器
            Desensitization desensitization = a.getAnnotation(Desensitization.class);
            if (desensitization != null) {
                log.trace("字段{}使用脱敏序列化，脱敏类型: {}，脱敏起始位置: {}，脱敏截止位置: {}，遮罩字符: {}", a.getName(), desensitization.value(), desensitization.start(), desensitization.end(), desensitization.mask());
                return new DesensitizationJacksonSerializer(desensitization.value(), desensitization.start(), desensitization.end(), desensitization.mask());
            }
        }
        return super.findSerializer(a);
    }
}
