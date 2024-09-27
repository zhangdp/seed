package io.github.seed.common.component;

import lombok.Getter;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.LinkedHashMap;

/**
 * 事件上下文
 *
 * @author zhangdp
 * @since 2024/9/17
 */
@Getter
public class EventContext {

    /**
     * 附加数据
     */
    private final String tag;
    /**
     * 结果
     */
    private final Object result;
    /**
     * 参数
     */
    private final LinkedHashMap<String, Object> params;
    /**
     * 事件产生时间
     */
    private final long timestamp;
    /**
     * 事件来源
     */
    private final MethodSignature source;

    public EventContext(MethodSignature source, String tag, LinkedHashMap<String, Object> params, Object result) {
        this.tag = tag;
        this.params = params;
        this.result = result;
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 根据下标获取参数
     *
     * @param index
     * @return
     */
    public Object getParam(int index) {
        return params.sequencedValues().toArray()[index];
    }

    /**
     * 获取第一个符合类型的参数
     *
     * @param clazz
     * @return
     */
    public Object getFirstParam(Class<?> clazz) {
        for (Object value : params.sequencedValues()) {
            if (clazz.isInstance(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据名称获取参数
     *
     * @param name
     * @return
     */
    public Object getParam(String name) {
        return params.get(name);
    }


}
