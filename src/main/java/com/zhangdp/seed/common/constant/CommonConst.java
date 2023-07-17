package com.zhangdp.seed.common.constant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 2023/4/3 公共常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface CommonConst {

    /**
     * 是
     */
    int YES_TRUE = 1;

    /**
     * 否
     */
    int NO_FALSE = 0;

    /**
     * 最大LocalDate日期 - 9999-12-31
     */
    LocalDate MAX_LOCAL_DATE = LocalDate.of(9999, 12, 31);

    /**
     * 最大LocalTime时间 - 23:59:59.999
     */
    LocalTime MAX_LOCAL_TIME = LocalTime.of(23, 59, 59, 999);
    // LocalTime.MAX 纳秒是999_999_999，存到数据库会溢出变成下一天0点
    // LocalTime MAX_LOCAL_TIME = LocalTime.MAX;

    /**
     * 最大LocalDateTIme - 9999-12-31 23:59:59.999
     */
    LocalDateTime MAX_LOCAL_DATE_TIME = LocalDateTime.of(MAX_LOCAL_DATE, MAX_LOCAL_TIME);

    /**
     * 状态正常
     */
    int GOOD = 0;

    /**
     * 状态异常
     */
    int BAD = 1;

    /**
     * 日期格式化
     */
    String DATE_FORMATTER = "yyyy-MM-dd";

    /**
     * 时间格式化，注意[.SSS]可选的毫秒值
     */
    String TIME_FORMATTER = "HH:mm:ss[.SSS]";

    /**
     * 日期时间默认格式化
     */
    String DATETIME_FORMATTER = DATE_FORMATTER + " " + TIME_FORMATTER;

    /**
     * 默认根id
     */
    long ROOT_ID = 0L;

    /**
     * 默认分页页数1
     */
    int PAGE = 1;

    /**
     * 默认分页一页条数
     */
    int PAGE_SIZE = 10;

    /**
     * 错误描述最长多少个字符
     */
    int MAX_ERROR_MSG_LENGTH = 50;

    /**
     * 接口返回状态码成功
     */
    int RESULT_SUCCESS = 0;

    /**
     * 编码
     */
    String CHARSET = "UTF-8";

    /**
     * Bearer
     */
    String BEARER_TYPE = "Bearer";
}
