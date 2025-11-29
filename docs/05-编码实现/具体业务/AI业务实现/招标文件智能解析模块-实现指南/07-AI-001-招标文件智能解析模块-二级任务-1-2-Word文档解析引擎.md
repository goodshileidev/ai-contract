# Python FastAPI AI 服务任务详细计划 - AI-001 - AI-001: 招标文件智能解析模块 - 二级任务 1.2: Word文档解析引擎

**预计工作量**: 2 人天
**完成进度**: 0% (0/5 类别)

#### 1.2.1 数据定义

**待完成任务**:
- [ ] 扩展 `ParsedDocument` 支持Word格式
- [ ] 定义 `WordSection` 数据结构
  ```python
  class WordSection(BaseModel):
      """Word文档章节"""
      level: int  # 标题级别 1-9
      title: str
      content: str
      style: str  # 样式名称
  ```

#### 1.2.2 前端实现

**待完成任务**:
- [ ] 文件上传组件支持 .docx 格式
- [ ] 解析结果展示支持Word文档结构
- [ ] 支持章节树形导航

#### 1.2.3 Java后端实现

**待完成任务**:
- [ ] 验证 BiddingDocument 实体支持Word文档
- [ ] 添加Word文档特定字段（如章节数量）

#### 1.2.4 Python后端实现

**待完成任务**:
- [ ] 实现 Word 解析服务
  ```python
  # apps/backend-python/app/services/ai/word_parser.py
  from docx import Document

  class WordParserService:
      async def parse_word(self, file_path: str) -> ParsedDocument:
          """
          解析Word文档
          需求编号: REQ-AI-001
          """
          doc = Document(file_path)

          sections = []
          for para in doc.paragraphs:
              if para.style.name.startswith('Heading'):
                  level = int(para.style.name.split()[-1])
                  sections.append({
                      "level": level,
                      "title": para.text,
                      "style": para.style.name
                  })
              else:
                  # 普通段落内容
                  pass

          return ParsedDocument(...)
  ```

- [ ] 创建 Celery 任务
  ```python
  @celery_app.task
  def parse_word_task(document_id: str, file_path: str):
      parser = WordParserService()
      result = await parser.parse_word(file_path)
      # ...
  ```

#### 1.2.5 部署配置

**待完成任务**:
- [ ] 确保 python-docx 已安装（已在 pyproject.toml）
- [ ] 无需额外系统依赖

---
