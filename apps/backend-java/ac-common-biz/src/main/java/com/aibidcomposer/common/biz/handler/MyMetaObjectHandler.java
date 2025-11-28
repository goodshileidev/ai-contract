package com.aibidcomposer.common.biz.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 元数据处理器
 * 自动填充创建时间、更新时间、创建人、更新人等字段
 *
 * 需求编号: REQ-JAVA-COMMON-BIZ-001
 * 创建时间: 2025-11-26
 * 创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
 *
 * @author AIBidComposer Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的填充策略
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 删除标志
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);

        // TODO: 从当前登录用户获取创建人ID
        // Long userId = SecurityUtils.getCurrentUserId();
        // this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        // this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
    }

    /**
     * 更新时的填充策略
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // TODO: 从当前登录用户获取更新人ID
        // Long userId = SecurityUtils.getCurrentUserId();
        // this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
    }
}
