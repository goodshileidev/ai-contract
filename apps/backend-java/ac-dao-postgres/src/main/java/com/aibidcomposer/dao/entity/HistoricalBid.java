package com.aibidcomposer.dao.entity;

import com.aibidcomposer.common.biz.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 历史标书实体
 *
 * 需求编号: REQ-JAVA-DAO-003
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "historical_bids", autoResultMap = true)
public class HistoricalBid extends BaseEntity {

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 招标日期
     */
    @TableField("bidding_date")
    private LocalDate biddingDate;

    /**
     * 提交日期
     */
    @TableField("submission_date")
    private LocalDate submissionDate;

    /**
     * 结果: won-中标, lost-未中标, pending-待定, withdrawn-撤回
     */
    @TableField("result")
    private String result;

    /**
     * 合同金额
     */
    @TableField("contract_amount")
    private BigDecimal contractAmount;

    /**
     * 投标金额
     */
    @TableField("bid_amount")
    private BigDecimal bidAmount;

    /**
     * 中标率(%)
     */
    @TableField("win_rate")
    private BigDecimal winRate;

    /**
     * 行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 关键要点(JSON数组)
     */
    @TableField(value = "key_points", typeHandler = JacksonTypeHandler.class)
    private List<String> keyPoints;

    /**
     * 成功因素
     */
    @TableField("success_factors")
    private String successFactors;

    /**
     * 经验教训
     */
    @TableField("lessons_learned")
    private String lessonsLearned;

    /**
     * 关联文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 是否可复用
     */
    @TableField("is_reusable")
    private Boolean isReusable;

    /**
     * 标签(JSON数组)
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 元数据(JSON)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}
