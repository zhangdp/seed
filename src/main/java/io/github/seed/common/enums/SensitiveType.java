package io.github.seed.common.enums;

import cn.hutool.v7.core.data.masking.MaskingUtil;
import cn.hutool.v7.core.text.StrUtil;

import java.util.function.Function;

/**
 * 2023/8/14 需要脱敏的敏感数据类型枚举
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum SensitiveType {

    /**
     * 自定义
     */
    CUSTOMER(str -> {
        throw new UnsupportedOperationException("自定义类型需自行实现脱敏逻辑");
    }),
    /**
     * 中文名
     */
    CHINESE_NAME(MaskingUtil::chineseName),
    /**
     * 身份证号
     */
    CITIZEN_ID(str -> MaskingUtil.idCardNum(str, 1, 2)),
    /**
     * 电话
     */
    TEL(MaskingUtil::fixedPhone),
    /**
     * 手机号
     */
    MOBILE(MaskingUtil::mobilePhone),
    /**
     * 地址
     */
    ADDRESS(str -> MaskingUtil.address(str, 8)),
    /**
     * 电子邮件
     */
    EMAIL(MaskingUtil::email),
    /**
     * 密码
     */
    PASSWORD(MaskingUtil::password),
    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE(MaskingUtil::carLicense),
    /**
     * 银行卡
     */
    BANK_CARD(MaskingUtil::bankCard),
    /**
     * ipv4地址
     */
    IPV4(MaskingUtil::ipv4),
    /**
     * ipv6地址
     */
    IPV6(MaskingUtil::ipv6),
    /**
     * 只保留第一个字符
     */
    ONLY_FIRST(MaskingUtil::firstMask),
    /**
     * 认证头
     */
    AUTHORIZATION(str -> MaskingUtil.idCardNum(str, 15, 4)),
    /**
     * token
     */
    TOKEN(str -> MaskingUtil.idCardNum(str, 8, 4));

    /**
     * 脱敏实现
     */
    private final Function<String, String> desensitizer;

    SensitiveType(Function<String, String> desensitizer) {
        this.desensitizer = desensitizer;
    }

    /**
     * 获取脱敏实现
     *
     * @return
     */
    public Function<String, String> getDesensitizer() {
        return desensitizer;
    }
}
