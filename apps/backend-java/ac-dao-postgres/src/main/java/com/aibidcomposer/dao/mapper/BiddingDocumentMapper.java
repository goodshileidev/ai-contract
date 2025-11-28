package com.aibidcomposer.dao.mapper;

import com.aibidcomposer.dao.entity.BiddingDocument;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 招标文件Mapper接口
 *
 * 需求编号: REQ-JAVA-DAO-002
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Mapper
public interface BiddingDocumentMapper extends BaseMapper<BiddingDocument> {

    /**
     * 根据项目ID查询招标文件列表
     *
     * @param projectId 项目ID
     * @return 招标文件列表
     */
    List<BiddingDocument> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据文件哈希查询招标文件
     *
     * @param documentHash 文件哈希
     * @return 招标文件信息
     */
    Optional<BiddingDocument> findByDocumentHash(@Param("documentHash") String documentHash);

    /**
     * 根据解析状态查询招标文件列表
     *
     * @param parsedStatus 解析状态
     * @return 招标文件列表
     */
    List<BiddingDocument> findByParsedStatus(@Param("parsedStatus") String parsedStatus);

    /**
     * 统计项目的招标文件数量
     *
     * @param projectId 项目ID
     * @return 文件数量
     */
    int countByProjectId(@Param("projectId") Long projectId);
}
