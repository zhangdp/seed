package io.github.seed.common.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 2024/9/23 日期工具类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class DateUtils {

    /**
     * 时间单位格式化文本数组
     */
    private static final String[] TIME_UNIT_FORMAT = {"天", "小时", "分钟", "秒", "毫秒"};
    /**
     * 时间单位数组
     */
    private static final TimeUnit[] TIME_UNIT = {TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS};
    /**
     * 每个时间单位与上一级单位倍数
     */
    private static final int[] TIME_UNIT_MUL = {24, 60, 60, 1000};

    private DateUtils() {
    }

    /**
     * 时长单位格式化
     * <br>最大单位：天，最小单位：毫秒
     *
     * @param duration
     * @param timeUnit
     * @return
     */
    public static String formatDuration(long duration, TimeUnit timeUnit) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        int index = -1;
        for (int i = 0; i < TIME_UNIT.length; i++) {
            if (TIME_UNIT[i].equals(timeUnit)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new IllegalArgumentException("Unsupported time unit");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= index; i++) {
            long mul = 1;
            for (int k = i; k <= index - 1; k++) {
                mul *= TIME_UNIT_MUL[k];
            }
            long div = duration / mul;
            if (div > 0) {
                sb.append(div).append(TIME_UNIT_FORMAT[i]);
            }
            duration = duration % mul;
        }
        return sb.toString();
    }

    /**
     * 返回两个日期中间的日期列表，包含头尾
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> ranges(LocalDate startDate, LocalDate endDate) {
        return ranges(startDate, endDate, true, true);
    }

    /**
     * 返回两个日期中间的日期列表
     *
     * @param startDate
     * @param endDate
     * @param includeStart
     * @param includeEnd
     * @return
     */
    public static List<LocalDate> ranges(LocalDate startDate, LocalDate endDate, boolean includeStart, boolean includeEnd) {
        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("StartDate is not before EndDate");
        }
        List<LocalDate> list = new ArrayList<>();
        if (includeStart) {
            list.add(startDate);
        }
        LocalDate date = startDate.plusDays(1L);
        while (date.isBefore(endDate)) {
            list.add(date);
            date = date.plusDays(1L);
        }
        if (includeEnd) {
            list.add(endDate);
        }
        return list;
    }

}
