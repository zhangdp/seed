package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 2023/12/26 分页数据结果封装
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Schema(title = "分页数据")
public class PageData<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 默认页数1
     */
    public static final int PAGE = 1;
    /**
     * 默认每页条数10
     */
    public static final int SIZE = 10;

    /**
     * 当前页数据列表
     */
    @Schema(title = "数据列表")
    private final List<T> list;
    /**
     * 总数
     */
    @Schema(title = "总数")
    private final long total;
    /**
     * 当前页数
     */
    @Schema(title = "当前页数")
    private final long page;
    /**
     * 每页条数
     */
    @Schema(title = "每页条数")
    private final long size;

    /**
     * 使用默认页数、条数构造函数
     *
     * @param list
     * @param total
     */
    public PageData(List<T> list, long total) {
        this(list, total, PAGE, SIZE);
    }

    /**
     * 构造函数
     *
     * @param list
     * @param total
     * @param page
     * @param size
     */
    public PageData(List<T> list, long total, long page, long size) {
        if (size <= 0) {
            throw new IllegalArgumentException("\"size\" cannot be less than or equal to 0");
        }
        // 如果当前页小于1，则重置为第1页
        this.page = Math.max(page, 1);
        this.list = list == null ? Collections.emptyList() : list;
        this.total = Math.max(total, 0L);
        this.size = size;
    }

    /**
     * 上一页，当没有上一页时返回0
     *
     * @return
     */
    @Schema(title = "上一页")
    public long getPrevPage() {
        return page <= 1L ? 0L : page - 1L;
    }

    /**
     * 下一页，当没有下一页时返回0
     *
     * @return
     */
    @Schema(title = "下一页")
    public long getNextPage() {
        long tp = getTotalPages();
        return page >= tp ? 0L : page + 1L;
    }

    /**
     * 是否为第一页
     *
     * @return
     */
    @Schema(title = "是否为第一页")
    public boolean getIsFirstPage() {
        return page == 1L;
    }

    /**
     * 是否为最后一页
     *
     * @return
     */
    @Schema(title = "是否为最后一页")
    public boolean getIsLastPage() {
        return page == this.getTotalPages();
    }

    /**
     * 是否有前一页
     *
     * @return
     */
    @Schema(title = "是否有前一页")
    public boolean getHasPrevPage() {
        return page > 1;
    }

    /**
     * 是否有下一页
     *
     * @return
     */
    @Schema(title = "是否有下一页")
    public boolean getHasNextPage() {
        return page < this.getTotalPages();
    }

    /**
     * 总页数
     *
     * @return
     */
    @Schema(title = "总页数")
    public long getTotalPages() {
        if (total == 0L) {
            return 0;
        }
        return (int) (total / size) + (total % size > 0 ? 1L : 0L);
    }

}
