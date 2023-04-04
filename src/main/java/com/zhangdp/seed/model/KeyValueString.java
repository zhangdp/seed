package com.zhangdp.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 值为字符串类型key-value
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@Schema(description = "key-value(String类型)对象")
public class KeyValueString extends KevValue<String> implements Serializable {

    public KeyValueString(String key, String value) {
        super(key, value);
    }

}
