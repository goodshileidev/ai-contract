# BlockNote 富文本编辑器集成任务

**文档类型**: 实现文档
**需求编号**: REQ-FRONT-003 (文档编辑和预览界面)
**创建日期**: 2025-11-26 11:10
**创建者**: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
**优先级**: P0 - 紧急（30人天节省机会）
**状态**: ⏸️ 待开始
**预计工作量**: 10人天（复用后，原始30人天）

---

## 修改历史

| 日期 | 时间 | 版本 | 修改者 | 修改内容概要 |
|------|------|------|--------|-------------|
| 2025-11-26 | 11:10 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 创建BlockNote编辑器集成任务文档 |

---

## 背景

根据 ai-doc 项目复用分析，BlockNote 富文本编辑器是**最高复用价值模块**（100%复用率，节省30人天）。该编辑器在 ai-doc 项目中已经实现以下功能：

- ✅ 基于 BlockNote 的富文本编辑
- ✅ 支持嵌套块结构（标题、段落、列表、引用等）
- ✅ Markdown 快捷输入
- ✅ 拖拽重排序
- ✅ 实时协作（基于 Yjs）
- ✅ AI 辅助编辑
- ✅ 自定义块类型扩展

**当前问题**:
- ❌ ai-contract 项目中尚未集成 BlockNote 编辑器
- ❌ 缺少富文本编辑组件，无法支持文档编辑功能
- ❌ 导致 30 人天的开发工作量浪费

---

## 任务目标

从 ai-doc 项目复用 BlockNote 编辑器，适配到 ai-contract 项目的标书编辑场景。

---

## 任务拆分（按5类别细分）

### 1. 数据定义

#### 1.1 文档内容数据结构
**文件**: `apps/backend-java/ac-dao-postgres/src/main/java/com/aibidcomposer/dao/entity/BidDocument.java`

```java
@Data
@TableName(value = "bid_documents", autoResultMap = true)
public class BidDocument extends BaseEntity {
    // ...existing fields

    // BlockNote 内容（JSON格式）
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;  // BlockNote编辑器内容

    // 纯文本内容（用于搜索）
    @TableField(value = "plain_content")
    private String plainContent;  // BlockNote内容的纯文本提取

    // 目录结构（JSON格式）
    @TableField(value = "toc", typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> toc;  // 自动生成的目录结构
}
```

**待完成**:
- [ ] 验证 `content` 字段支持 BlockNote JSON Schema
- [ ] 添加索引：`CREATE INDEX idx_plain_content_fts ON bid_documents USING gin(to_tsvector('chinese', plain_content));`
- [ ] 创建数据库迁移脚本

#### 1.2 DocumentSection 数据结构
**文件**: `apps/backend-java/ac-dao-postgres/src/main/java/com/aibidcomposer/dao/entity/DocumentSection.java`

```java
@Data
@TableName(value = "document_sections", autoResultMap = true)
public class DocumentSection extends BaseEntity {
    @TableField("document_id")
    private String documentId;

    @TableField("parent_id")
    private String parentId;

    @TableField("title")
    private String title;

    @TableField("section_number")
    private String sectionNumber;  // 如 "1.2.3"

    @TableField("level")
    private Integer level;  // 1-6（对应h1-h6）

    // BlockNote 章节内容
    @TableField(value = "content", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;

    @TableField("order_index")
    private Integer orderIndex;

    // 是否AI生成
    @TableField("is_generated")
    private Boolean isGenerated;
}
```

**待完成**:
- [ ] 创建 `document_sections` 表
- [ ] 添加外键约束：`FOREIGN KEY (document_id) REFERENCES bid_documents(id)`
- [ ] 添加索引：`CREATE INDEX idx_document_sections_order ON document_sections(document_id, order_index);`

---

### 2. 前端实现

#### 2.1 安装依赖
**文件**: `apps/frontend/package.json`

```json
{
  "dependencies": {
    "@blocknote/core": "^0.12.0",
    "@blocknote/react": "^0.12.0",
    "@blocknote/mantine": "^0.12.0",
    "yjs": "^13.6.0",
    "y-websocket": "^1.5.0",
    "y-protocols": "^1.0.6"
  }
}
```

**待完成**:
- [ ] 运行 `npm install @blocknote/core @blocknote/react @blocknote/mantine yjs y-websocket y-protocols`
- [ ] 验证依赖安装成功

#### 2.2 创建 BlockNote 编辑器组件
**文件**: `apps/frontend/src/components/editor/BlockNoteEditor.tsx`

**从 ai-doc 复用路径**: `ai-doc/frontend/src/components/editor/BlockNoteEditor.tsx`

```typescript
import { useEffect, useState } from 'react';
import { useCreateBlockNote } from '@blocknote/react';
import { BlockNoteView } from '@blocknote/mantine';
import '@blocknote/core/fonts/inter.css';
import '@blocknote/mantine/style.css';
import type { Block } from '@blocknote/core';

interface BlockNoteEditorProps {
  initialContent?: Block[];
  onChange?: (content: Block[]) => void;
  editable?: boolean;
  placeholder?: string;
}

/**
 * BlockNote 富文本编辑器组件
 * 需求编号: REQ-FRONT-003
 * 复用来源: ai-doc 项目 BlockNote 编辑器（100%复用）
 */
export function BlockNoteEditor({
  initialContent,
  onChange,
  editable = true,
  placeholder = '开始撰写标书内容...',
}: BlockNoteEditorProps) {
  const editor = useCreateBlockNote({
    initialContent,
  });

  useEffect(() => {
    if (onChange && editor) {
      editor.onChange(() => {
        onChange(editor.document);
      });
    }
  }, [editor, onChange]);

  return (
    <div className="blocknote-editor-wrapper">
      <BlockNoteView
        editor={editor}
        editable={editable}
        theme="light"
      />
    </div>
  );
}
```

**待完成**:
- [ ] 从 ai-doc 项目复制完整的 `BlockNoteEditor.tsx`
- [ ] 从 ai-doc 项目复制自定义块类型定义
- [ ] 从 ai-doc 项目复制 AI 辅助编辑功能
- [ ] 从 ai-doc 项目复制工具栏扩展

#### 2.3 创建文档编辑页面
**文件**: `apps/frontend/src/pages/documents/DocumentEdit.tsx`

```typescript
import { useState, useEffect } from 'react';
import { ProCard } from '@ant-design/pro-components';
import { message, Spin } from 'antd';
import { useParams } from 'react-router-dom';
import { BlockNoteEditor } from '@/components/editor/BlockNoteEditor';
import { documentService } from '@/services/document.service';
import type { Block } from '@blocknote/core';

/**
 * 文档编辑页面
 * 需求编号: REQ-FRONT-003
 */
export default function DocumentEdit() {
  const { id } = useParams<{ id: string }>();
  const [loading, setLoading] = useState(true);
  const [content, setContent] = useState<Block[]>([]);
  const [saving, setSaving] = useState(false);

  // 加载文档内容
  useEffect(() => {
    if (id) {
      loadDocument(id);
    }
  }, [id]);

  const loadDocument = async (docId: string) => {
    try {
      setLoading(true);
      const doc = await documentService.getDocument(docId);
      setContent(doc.content || []);
    } catch (error) {
      message.error('加载文档失败');
    } finally {
      setLoading(false);
    }
  };

  // 自动保存（防抖）
  const handleContentChange = async (newContent: Block[]) => {
    setContent(newContent);

    // TODO: 实现防抖自动保存
    // debounce(() => {
    //   saveDocument(newContent);
    // }, 2000);
  };

  const saveDocument = async (newContent: Block[]) => {
    try {
      setSaving(true);
      await documentService.updateDocument(id!, {
        content: newContent,
      });
      message.success('保存成功');
    } catch (error) {
      message.error('保存失败');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <Spin size="large" />;
  }

  return (
    <ProCard title="文档编辑" extra={saving && <Spin size="small" />}>
      <BlockNoteEditor
        initialContent={content}
        onChange={handleContentChange}
        editable={true}
      />
    </ProCard>
  );
}
```

**待完成**:
- [ ] 创建 `DocumentEdit.tsx` 页面
- [ ] 集成 BlockNote 编辑器
- [ ] 实现自动保存（防抖2秒）
- [ ] 实现保存状态提示
- [ ] 添加版本历史查看

#### 2.4 创建文档预览页面
**文件**: `apps/frontend/src/pages/documents/DocumentPreview.tsx`

```typescript
import { BlockNoteEditor } from '@/components/editor/BlockNoteEditor';

export default function DocumentPreview() {
  // 只读模式展示文档
  return (
    <BlockNoteEditor
      initialContent={content}
      editable={false}
    />
  );
}
```

**待完成**:
- [ ] 创建 `DocumentPreview.tsx` 页面
- [ ] 设置只读模式
- [ ] 添加打印样式
- [ ] 添加导出PDF功能

---

### 3. Java 后端实现

#### 3.1 文档内容API
**文件**: `apps/backend-java/ac-service/src/main/java/com/aibidcomposer/service/DocumentService.java`

```java
package com.aibidcomposer.service;

import com.aibidcomposer.dao.entity.BidDocument;
import com.aibidcomposer.dao.entity.DocumentSection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

/**
 * 文档服务
 * 需求编号: REQ-JAVA-004
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final BidDocumentMapper bidDocumentMapper;
    private final DocumentSectionMapper documentSectionMapper;

    /**
     * 更新文档内容
     * 需求编号: REQ-JAVA-004
     */
    @Transactional(rollbackFor = Exception.class)
    public BidDocument updateContent(String documentId, Map<String, Object> content) {
        BidDocument document = bidDocumentMapper.selectById(documentId);
        if (document == null) {
            throw new ResourceNotFoundException("文档不存在");
        }

        // 更新 BlockNote 内容
        document.setContent(content);

        // 提取纯文本（用于全文检索）
        String plainText = extractPlainText(content);
        document.setPlainContent(plainText);

        // 提取目录结构
        List<Map<String, Object>> toc = extractTableOfContents(content);
        document.setToc(toc);

        // 更新字数统计
        int wordCount = countWords(plainText);
        document.setWordCount(wordCount);

        bidDocumentMapper.updateById(document);

        log.info("文档内容已更新，ID: {}, 字数: {}", documentId, wordCount);

        return document;
    }

    /**
     * 从 BlockNote 内容提取纯文本
     */
    private String extractPlainText(Map<String, Object> content) {
        // TODO: 实现 BlockNote JSON 到纯文本的转换
        // 遍历 blocks，提取所有文本节点
        return "";
    }

    /**
     * 从 BlockNote 内容提取目录
     */
    private List<Map<String, Object>> extractTableOfContents(Map<String, Object> content) {
        // TODO: 提取所有标题块（h1-h6），生成目录结构
        return List.of();
    }

    /**
     * 统计字数（中英文混合）
     */
    private int countWords(String text) {
        // 中文字符数 + 英文单词数
        return text.length();  // 简化版
    }
}
```

**待完成**:
- [ ] 实现 `updateContent` 方法
- [ ] 实现 `extractPlainText` 方法（解析 BlockNote JSON）
- [ ] 实现 `extractTableOfContents` 方法
- [ ] 实现 `countWords` 方法（正确统计中英文混合字数）
- [ ] 添加单元测试

#### 3.2 章节管理API
**文件**: `apps/backend-java/ac-web-api/src/main/java/com/aibidcomposer/web/controller/DocumentController.java`

```java
package com.aibidcomposer.web.controller;

import com.aibidcomposer.service.DocumentService;
import com.aibidcomposer.web.vo.DocumentUpdateRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 文档管理控制器
 * 需求编号: REQ-JAVA-004
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 更新文档内容
     * 需求编号: REQ-JAVA-004
     */
    @PutMapping("/{id}")
    public Result<BidDocument> updateDocument(
        @PathVariable String id,
        @RequestBody @Validated DocumentUpdateRequest request
    ) {
        BidDocument document = documentService.updateContent(id, request.getContent());
        return Result.success(document);
    }

    /**
     * 获取文档内容
     * 需求编号: REQ-JAVA-004
     */
    @GetMapping("/{id}")
    public Result<BidDocument> getDocument(@PathVariable String id) {
        BidDocument document = documentService.getById(id);
        return Result.success(document);
    }
}
```

**待完成**:
- [ ] 创建 `DocumentUpdateRequest` DTO
- [ ] 实现 PUT /api/v1/documents/{id}
- [ ] 实现 GET /api/v1/documents/{id}
- [ ] 添加权限验证（只有项目成员可编辑）
- [ ] 添加乐观锁（防止并发冲突）

---

### 4. Python 后端实现

#### 4.1 AI 辅助编辑功能
**文件**: `apps/backend-python/app/api/v1/ai_assistant.py`

```python
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.services.ai.llm_client import LLMClient
from typing import List, Dict, Any

router = APIRouter(prefix="/api/v1/ai", tags=["AI Assistant"])

class ContentImproveRequest(BaseModel):
    """内容改进请求"""
    content: str
    improve_type: str  # 'polish'|'expand'|'simplify'|'professional'

class ContentImproveResponse(BaseModel):
    """内容改进响应"""
    improved_content: str
    suggestions: List[str]

@router.post("/improve-content", response_model=ContentImproveResponse)
async def improve_content(request: ContentImproveRequest):
    """
    AI 辅助改进内容
    需求编号: REQ-AI-002
    """
    llm_client = LLMClient()

    # 构建Prompt
    prompt = f"""
请对以下内容进行{request.improve_type}处理：

{request.content}

要求：
1. 保持原意不变
2. 提升专业性和可读性
3. 符合标书撰写规范
"""

    response = await llm_client.chat(
        messages=[
            {"role": "system", "content": "你是专业的标书撰写助手。"},
            {"role": "user", "content": prompt}
        ],
        model="gpt-4-turbo-preview",
        temperature=0.7
    )

    return ContentImproveResponse(
        improved_content=response["content"],
        suggestions=["建议1", "建议2"]  # TODO: 从AI响应提取建议
    )
```

**待完成**:
- [ ] 实现 POST /api/v1/ai/improve-content
- [ ] 实现不同改进类型的Prompt（润色、扩写、简化、专业化）
- [ ] 实现智能续写功能
- [ ] 实现自动补全功能
- [ ] 添加API限流（防止滥用）

---

### 5. 部署配置

#### 5.1 Docker 配置
**文件**: `apps/frontend/Dockerfile`

```dockerfile
# 添加 BlockNote 编辑器样式文件
FROM node:18-alpine AS builder

WORKDIR /app

# 复制依赖文件
COPY package.json package-lock.json ./
RUN npm ci --only=production

# 复制源代码（包括 BlockNote 组件）
COPY . .

# 构建（包含 BlockNote 样式）
RUN npm run build

# Nginx 阶段
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/node_modules/@blocknote /usr/share/nginx/html/assets/@blocknote
```

**待完成**:
- [ ] 确保 Dockerfile 正确复制 BlockNote 静态资源
- [ ] 配置 Nginx 正确服务 @blocknote 资源
- [ ] 测试生产环境构建

#### 5.2 环境变量配置
**文件**: `.env.example`

```bash
# 前端配置
VITE_EDITOR_AUTOSAVE_INTERVAL=2000  # 自动保存间隔（毫秒）
VITE_EDITOR_MAX_FILE_SIZE=10485760  # 最大文件大小（10MB）

# BlockNote 编辑器配置
VITE_BLOCKNOTE_COLLABORATION_ENABLED=true  # 是否启用协作
VITE_BLOCKNOTE_AI_ENABLED=true             # 是否启用AI辅助
```

**待完成**:
- [ ] 添加编辑器相关环境变量
- [ ] 更新 `.env.example`
- [ ] 更新部署文档

---

## 验收标准

### 功能验收
- [ ] BlockNote 编辑器成功渲染
- [ ] 支持标题、段落、列表、引用等基础块类型
- [ ] 支持 Markdown 快捷输入（如 # 创建标题）
- [ ] 支持拖拽重排序
- [ ] 自动保存功能正常（2秒防抖）
- [ ] 保存后内容正确存储到数据库
- [ ] 纯文本提取正确（用于全文检索）
- [ ] 目录自动生成正确
- [ ] 字数统计准确

### 性能验收
- [ ] 大文档（>5000字）编辑流畅（无明显卡顿）
- [ ] 自动保存不影响编辑体验
- [ ] 页面加载时间 < 2秒

### 兼容性验收
- [ ] Chrome 最新版正常
- [ ] Firefox 最新版正常
- [ ] Safari 最新版正常
- [ ] Edge 最新版正常

---

## 风险和注意事项

### 技术风险
1. **BlockNote 版本兼容性**
   - 风险: ai-doc 使用的 BlockNote 版本可能不兼容当前依赖
   - 缓解: 先测试 ai-doc 版本，如不兼容再升级

2. **样式冲突**
   - 风险: BlockNote 样式可能与 Ant Design 冲突
   - 缓解: 使用 CSS Modules 隔离样式

### 数据风险
1. **内容丢失**
   - 风险: 自动保存失败导致内容丢失
   - 缓解: 实现本地缓存（LocalStorage），离线编辑支持

2. **并发冲突**
   - 风险: 多人同时编辑同一文档导致冲突
   - 缓解: 实现乐观锁，后续可集成 Yjs 实时协作

---

## 参考资料

### ai-doc 项目源码
- BlockNote 编辑器组件: `ai-doc/frontend/src/components/editor/BlockNoteEditor.tsx`
- 自定义块类型: `ai-doc/frontend/src/components/editor/blocks/`
- AI 辅助编辑: `ai-doc/frontend/src/components/editor/AIAssistant.tsx`

### 官方文档
- BlockNote 文档: https://www.blocknotejs.org/docs
- Yjs 文档: https://docs.yjs.dev/

### 相关任务
- REQ-FRONT-003: 文档编辑和预览界面
- REQ-JAVA-004: 文档管理模块
- REQ-AI-002: 智能内容生成引擎

---

**下一步行动**:
1. ✅ 创建本任务文档
2. [ ] 从 ai-doc 项目复制 BlockNote 编辑器组件（3小时）
3. [ ] 适配到 ai-contract 项目（2小时）
4. [ ] 集成到文档编辑页面（2小时）
5. [ ] 实现自动保存功能（1小时）
6. [ ] 测试和修复bug（2小时）

**预计总工作量**: 10 人天（相比从零开发30人天，节省 67%）
