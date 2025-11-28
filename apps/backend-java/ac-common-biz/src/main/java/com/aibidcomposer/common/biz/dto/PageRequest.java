package com.aibidcomposer.common.biz.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页请求参数
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小最小为1")
    @Max(value = 100, message = "每页大小最大为100")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式（asc/desc）
     */
    private String sortOrder = "desc";

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 获取MyBatis Plus的起始位置
     *
     * @return 起始位置
     */
    public long getOffset() {
        return (long) (page - 1) * pageSize;
    }

    /**
     * 获取MyBatis Plus的每页大小
     *
     * @return 每页大小
     */
    public long getLimit() {
        return pageSize;
    }
}
