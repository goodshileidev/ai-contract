package com.aibidcomposer.common.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Pagination Result Class
 *
 * <p>Represents pagination results containing list data and page information.</p>
 *
 * <p>Features:
 * <ul>
 *   <li>Generic type support for any data type</li>
 *   <li>Contains page number, page size, total pages and total records</li>
 *   <li>Serializable for caching and session storage</li>
 * </ul>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-004</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 * @param <T> the type of elements in this page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * List of items in current page
     */
    private List<T> list;

    /**
     * Total number of pages
     */
    private Integer totalPage;

    /**
     * Total number of records
     */
    private Integer totalRow;

    /**
     * Number of items per page
     */
    private Integer pageSize;

    /**
     * Current page number (1-based)
     */
    private Integer pageNo;

    /**
     * Creates a new Page with specified parameters
     *
     * @param list the list of items
     * @param pageSize the page size
     * @param totalPage the total number of pages
     * @param totalRow the total number of records
     * @param <T> the type of elements
     * @return new Page instance
     */
    public static <T> Page<T> of(List<T> list, Integer pageSize, Integer totalPage, Integer totalRow) {
        return new Page<>(list, totalPage, totalRow, pageSize, null);
    }

    /**
     * Creates a new Page with specified parameters including page number
     *
     * @param list the list of items
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param totalRow the total number of records
     * @param <T> the type of elements
     * @return new Page instance
     */
    public static <T> Page<T> of(List<T> list, Integer pageNo, Integer pageSize, Integer totalRow) {
        int totalPage = (totalRow + pageSize - 1) / pageSize;
        return new Page<>(list, totalPage, totalRow, pageSize, pageNo);
    }

    /**
     * Creates an empty page
     *
     * @param pageNo the current page number
     * @param pageSize the page size
     * @param <T> the type of elements
     * @return empty Page instance
     */
    public static <T> Page<T> empty(Integer pageNo, Integer pageSize) {
        return new Page<>(Collections.emptyList(), 0, 0, pageSize, pageNo);
    }

    /**
     * Checks if this is the first page
     *
     * @return true if current page is the first page
     */
    public boolean isFirst() {
        return pageNo != null && pageNo == 1;
    }

    /**
     * Checks if this is the last page
     *
     * @return true if current page is the last page
     */
    public boolean isLast() {
        return pageNo != null && totalPage != null && pageNo.equals(totalPage);
    }

    /**
     * Checks if there is a next page
     *
     * @return true if there is a next page
     */
    public boolean hasNext() {
        return pageNo != null && totalPage != null && pageNo < totalPage;
    }

    /**
     * Checks if there is a previous page
     *
     * @return true if there is a previous page
     */
    public boolean hasPrevious() {
        return pageNo != null && pageNo > 1;
    }
}
