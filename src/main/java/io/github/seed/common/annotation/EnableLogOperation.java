package io.github.seed.common.annotation;

import io.github.seed.common.aspect.RecordOperationLogAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 2023/5/27 开启记录操作日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RecordOperationLogAspect.class)
public @interface EnableLogOperation {
}
