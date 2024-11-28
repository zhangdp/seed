package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 使用map作为查询参数的分页查询对象
 * <br>不应使用map，英使用确切字段的对象作为参数
 *
 * @author zhangdp
 * @see PageQuery
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(title = "map作为参数的分页查询对象")
@Deprecated
public class MapPageQuery extends BasePageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 动态的业务查询参数
     */
    private Map<String, Object> params = new LinkedHashMap<>();

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Object addParam(String key, Object value) {
        return this.params.put(key, value);
    }

    /**
     * 移除参数
     *
     * @param key
     * @return
     */
    public Object removeParam(String key) {
        return this.params.remove(key);
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return
     */
    public Object paramValue(String key) {
        return this.params.get(key);
    }

    /**
     * 获取参数值，如果不存在则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Object paramValue(String key, Object defaultValue) {
        return this.params.getOrDefault(key, defaultValue);
    }

}
