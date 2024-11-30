package io.github.seed.common.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import org.dromara.hutool.core.text.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2024/9/27 mybatis-plus 工具类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class MybatisPlusHelper {

    /**
     * 将orderBy（如a asc, b desc）字符串转为mybatis-plus分页排序列表
     *
     * @param orderBy
     * @return
     */
    public static List<OrderItem> toOrderItems(String orderBy) {
        return toOrderItems(orderBy, null);
    }

    /**
     * 将orderBy（如a asc, b desc）字符串转为mybatis-plus分页排序列表
     *
     * @param orderBy
     * @param defaultOrderBy
     * @return
     */
    public static List<OrderItem> toOrderItems(String orderBy, String defaultOrderBy) {
        String ob = StrUtil.isBlank(orderBy) ? defaultOrderBy : orderBy;
        if (StrUtil.isBlank(ob)) {
            return Collections.emptyList();
        }
        String[] orders = ob.split(",");
        List<OrderItem> orderItems = new ArrayList<>(orders.length);
        for (String order : orders) {
            if (StrUtil.isBlank(order)) {
                continue;
            }
            String[] arr = order.trim().split(" ");
            String column = arr[0].trim();
            if (column.isEmpty()) {
                continue;
            }
            OrderItem item = new OrderItem();
            item.setColumn(column);
            if (arr.length > 1 && StrUtil.containsIgnoreCase(arr[1], "desc")) {
                item.setAsc(false);
            }
            orderItems.add(item);
        }
        return orderItems;
    }

}
