package io.github.seed.common.data;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 业务事件
 *
 * @author zhangdp
 * @since 2024/9/17
 */
@Getter
public class ServiceEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private final String type;
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

    public ServiceEvent(Object source, String type, String tag, LinkedHashMap<String, Object> params, Object result) {
        super(source);
        this.type = type;
        this.tag = tag;
        this.params = params;
        this.result = result;
        this.source = source;
    }

    /**
     * 是否有参数
     *
     * @return
     */
    public boolean hasParam() {
        return params != null && !params.isEmpty();
    }

    /**
     * 是否存在某个名称的参数
     *
     * @param name
     * @return
     */
    public boolean hasParam(String name) {
        return this.hasParam() && this.params.containsKey(name);
    }

    /**
     * 获取参数个数
     *
     * @return
     */
    public int paramCount() {
        return this.hasParam() ? this.params.size() : 0;
    }

    /**
     * 根据下标获取参数
     *
     * @param index
     * @return
     */
    public Object getParam(int index) {
        int i = 0;
        Collection<Object> values = params.values();
        if (index < 0 || index >= params.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        for (Object v : values) {
            if (i == index) {
                return v;
            }
            i++;
        }
        return null;
    }

    /**
     * 获取第一个符合类型的参数
     *
     * @param clazz
     * @return
     */
    public Object getFirstParam(Class<?> clazz) {
        for (Object value : params.values()) {
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
