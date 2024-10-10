package io.github.seed.common.enums;

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
    CUSTOMER,
    /**
     * 用户id
     */
    USER_ID,
    /**
     * 中文名
     */
    CHINESE_NAME,
    /**
     * 身份证号
     */
    CITIZEN_ID,
    /**
     * 电话
     */
    TEL,
    /**
     * 手机号
     */
    MOBILE,
    /**
     * 地址
     */
    ADDRESS,
    /**
     * 电子邮件
     */
    EMAIL,
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE,
    /**
     * 银行卡
     */
    BANK_CARD,
    /**
     * ipv4地址
     */
    IPV4,
    /**
     * ipv6地址
     */
    IPV6,
    /**
     * 只保留第一个字符
     */
    ONLY_FIRST

}
