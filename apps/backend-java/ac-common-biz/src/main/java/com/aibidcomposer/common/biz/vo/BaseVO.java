package com.aibidcomposer.common.biz.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础VO（视图对象）
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
public abstract class BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private LocalDateTime updateTime;
}
