package io.github.seed.common.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.seed.common.enums.SensitiveType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.data.MaskingUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.io.IOException;

/**
 * 2023/8/14 脱敏jackson序列化
 *
 * @author zhangdp
 * @since 1.0.0
 */
@AllArgsConstructor
@Slf4j
public class DesensitizationJacksonSerializer extends JsonSerializer<String> {

    /**
     * 脱敏类型
     */
    private final SensitiveType type;
    /**
     * 脱敏起点
     */
    private final int start;
    /**
     * 脱敏终点
     */
    private final int end;
    /**
     * 脱敏替换字符
     * todo 受限于hutool的api，暂时固定是*
     */
    private final char mask;

    @Override
    public void serialize(String str, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String ret = null;
        switch (type) {
            // 自定义类型脱敏
            case CUSTOMER -> ret = StrUtil.hide(str, start, end);
            // userId脱敏
            case USER_ID -> ret = String.valueOf(MaskingUtil.userId());
            // 中文姓名脱敏
            case CHINESE_NAME -> ret = MaskingUtil.chineseName(str);
            // 身份证脱敏
            case CITIZEN_ID -> ret = MaskingUtil.idCardNum(str, 1, 2);
            // 固定电话脱敏
            case TEL -> ret = MaskingUtil.fixedPhone(str);
            // 手机号脱敏
            case MOBILE -> ret = MaskingUtil.mobilePhone(str);
            // 地址脱敏
            case ADDRESS -> ret = MaskingUtil.address(str, 8);
            // 邮箱脱敏
            case EMAIL -> ret = MaskingUtil.email(str);
            // 密码脱敏
            case PASSWORD -> ret = MaskingUtil.password(str);
            // 中国车牌脱敏
            case CAR_LICENSE -> ret = MaskingUtil.carLicense(str);
            // 银行卡脱敏
            case BANK_CARD -> ret = MaskingUtil.bankCard(str);
            // ipv4脱敏
            case IPV4 -> ret = MaskingUtil.ipv4(str);
            // ipv6脱敏
            case IPV6 -> ret = MaskingUtil.ipv6(str);
            // 只保留首字母
            case ONLY_FIRST -> ret = MaskingUtil.firstMask(str);
            // 其他：抛异常
            default -> throw new IllegalArgumentException("Unsupported desensitization type: " + type);
        }
        jsonGenerator.writeString(ret);
    }

}
