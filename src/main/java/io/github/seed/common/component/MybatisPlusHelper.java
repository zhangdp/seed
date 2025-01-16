package io.github.seed.common.component;

import com.baomidou.mybatisplus.core.metadata.OrderItem;

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
        String ob = orderBy == null || (orderBy = orderBy.trim()).isEmpty() ? defaultOrderBy : orderBy;
        if (ob == null || (ob = ob.trim()).isEmpty()) {
            return Collections.emptyList();
        }
        String[] orders = ob.replaceAll("\\s+", " ").split(",");
        List<OrderItem> orderItems = new ArrayList<>(orders.length);
        for (String order : orders) {
            if (order == null || (order = order.trim()).isEmpty()) {
                continue;
            }
            String[] arr = order.split(" ");
            String column = arr[0].trim();
            if (column.isEmpty()) {
                continue;
            }
            OrderItem item = new OrderItem();
            item.setColumn(column);
            if (arr.length > 1 && arr[1].toUpperCase().contains("DESC")) {
                item.setAsc(false);
            }
            orderItems.add(item);
        }
        return orderItems;
    }

}
