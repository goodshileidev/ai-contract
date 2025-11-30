# Python FastAPI AI 服务任务详细计划 - AI-001 - AI-001: 招标文件智能解析模块 - 二级任务 1.3: Excel表格解析引擎

**预计工作量**: 2 人天
**完成进度**: 0% (0/5 类别)

#### 1.3.1 数据定义

**待完成任务**:
- [ ] 定义 `ExcelSheet` 数据结构
  ```python
  class ExcelSheet(BaseModel):
      """Excel工作表"""
      sheet_name: str
      rows: List[List[Any]]  # 二维数组
      header: List[str]
      total_rows: int
      total_columns: int
  ```

#### 1.3.2 前端实现

**待完成任务**:
- [ ] 文件上传组件支持 .xlsx 格式
- [ ] Excel表格预览组件（ProTable）
- [ ] 多工作表切换展示

#### 1.3.3 Java后端实现

**待完成任务**:
- [ ] BiddingDocument 实体支持Excel文档

#### 1.3.4 Python后端实现

**待完成任务**:
- [ ] 实现 Excel 解析服务
  ```python
  # apps/backend-python/app/services/ai/excel_parser.py
  import openpyxl

  class ExcelParserService:
      async def parse_excel(self, file_path: str) -> ParsedDocument:
          """
          解析Excel文档
          需求编号: REQ-AI-001
          """
          wb = openpyxl.load_workbook(file_path)

          sheets = []
          for sheet in wb.worksheets:
              rows = []
              for row in sheet.iter_rows(values_only=True):
                  rows.append(list(row))

              sheets.append({
                  "sheet_name": sheet.title,
                  "rows": rows,
                  "total_rows": sheet.max_row,
                  "total_columns": sheet.max_column
              })

          return ParsedDocument(...)
  ```

#### 1.3.5 部署配置

**待完成任务**:
- [ ] 确保 openpyxl 已安装（已在 pyproject.toml）

---
