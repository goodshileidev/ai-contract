---
文档类型: 架构文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-15
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 技术栈调整方案 - 3️⃣ React + Ant Design Pro

### 3.1 架构调整

**✅ 完全兼容，体验更优**

```yaml
Ant Design Pro优势:
  开箱即用:
    - 完整的中后台解决方案
    - 预置权限路由
    - 内置国际化
    - ProComponents高级组件

  企业级组件:
    - ProTable (高级表格)
    - ProForm (高级表单)
    - ProLayout (专业布局)
    - ProDescriptions (详情页)

  最佳实践:
    - Umi 4框架
    - 约定式路由
    - 数据流方案
    - 请求和错误处理
```

### 3.2 项目结构

```
frontend/
├── config/
│   ├── config.ts              # Umi配置
│   ├── routes.ts              # 路由配置
│   └── proxy.ts               # 代理配置
├── src/
│   ├── .umi/                  # Umi临时文件
│   ├── components/            # 通用组件
│   │   ├── RightContent/
│   │   ├── Footer/
│   │   └── ...
│   ├── pages/                 # 页面
│   │   ├── User/              # 用户相关页面
│   │   │   ├── Login/
│   │   │   └── Register/
│   │   ├── Project/           # 项目管理
│   │   │   ├── List/
│   │   │   ├── Detail/
│   │   │   └── Create/
│   │   ├── Document/          # 文档管理
│   │   │   ├── Editor/
│   │   │   └── Version/
│   │   ├── Template/          # 模板管理
│   │   └── Dashboard/         # 仪表盘
│   ├── services/              # API服务
│   │   ├── api.ts             # API配置
│   │   ├── user.ts
│   │   ├── project.ts
│   │   ├── document.ts
│   │   └── ai.ts
│   ├── models/                # 数据模型(dva)
│   │   ├── user.ts
│   │   └── global.ts
│   ├── utils/                 # 工具函数
│   │   ├── request.ts
│   │   └── authority.ts
│   ├── access.ts              # 权限定义
│   ├── app.tsx                # 运行时配置
│   └── global.tsx             # 全局配置
├── package.json
└── tsconfig.json
```

### 3.3 核心代码示例

**ProTable示例 - 项目列表**
```typescript
// src/pages/Project/List/index.tsx
import { PlusOutlined } from '@ant-design/icons';
import { ProTable, ActionType } from '@ant-design/pro-components';
import { Button } from 'antd';
import { useRef } from 'react';
import { getProjects, type ProjectItem } from '@/services/project';

export default function ProjectList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns<ProjectItem>[] = [
    {
      title: '项目编号',
      dataIndex: 'code',
      width: 120,
      fixed: 'left',
    },
    {
      title: '项目名称',
      dataIndex: 'name',
      width: 200,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueEnum: {
        draft: { text: '草稿', status: 'Default' },
        in_progress: { text: '进行中', status: 'Processing' },
        completed: { text: '已完成', status: 'Success' },
      },
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: 100,
      valueEnum: {
        low: { text: '低', status: 'Default' },
        medium: { text: '中', status: 'Warning' },
        high: { text: '高', status: 'Error' },
      },
    },
    {
      title: '预算 (万元)',
      dataIndex: 'budget',
      width: 120,
      valueType: 'money',
      search: false,
    },
    {
      title: '中标概率',
      dataIndex: 'winProbability',
      width: 120,
      valueType: 'percent',
      search: false,
    },
    {
      title: '截止日期',
      dataIndex: 'submissionDeadline',
      width: 150,
      valueType: 'dateTime',
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      valueType: 'option',
      render: (_, record) => [
        <a key="view" onClick={() => history.push(`/project/${record.id}`)}>
          查看
        </a>,
        <a key="edit">编辑</a>,
        <a key="delete" style={{ color: 'red' }}>
          删除
        </a>,
      ],
    },
  ];

  return (
    <ProTable<ProjectItem>
      columns={columns}
      actionRef={actionRef}
      request={async (params, sort, filter) => {
        const response = await getProjects({
          page: params.current,
          pageSize: params.pageSize,
          ...params,
        });
        return {
          data: response.data.items,
          success: true,
          total: response.data.total,
        };
      }}
      rowKey="id"
      search={{
        labelWidth: 'auto',
      }}
      pagination={{
        pageSize: 20,
      }}
      dateFormatter="string"
      headerTitle="项目列表"
      toolBarRender={() => [
        <Button
          key="create"
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => history.push('/project/create')}
        >
          新建项目
        </Button>,
      ]}
    />
  );
}
```

**ProForm示例 - 创建项目**
```typescript
// src/pages/Project/Create/index.tsx
import { ProForm, ProFormText, ProFormSelect, ProFormDateTimePicker } from '@ant-design/pro-components';
import { message } from 'antd';
import { createProject } from '@/services/project';

export default function CreateProject() {
  return (
    <ProForm
      onFinish={async (values) => {
        try {
          await createProject(values);
          message.success('项目创建成功');
          history.push('/project');
        } catch (error) {
          message.error('项目创建失败');
        }
      }}
    >
      <ProFormText
        name="name"
        label="项目名称"
        placeholder="请输入项目名称"
        rules={[{ required: true, message: '请输入项目名称' }]}
      />

      <ProFormSelect
        name="priority"
        label="优先级"
        valueEnum={{
          low: '低',
          medium: '中',
          high: '高',
        }}
        placeholder="请选择优先级"
        rules={[{ required: true }]}
      />

      <ProFormDateTimePicker
        name="submissionDeadline"
        label="投标截止时间"
        rules={[{ required: true }]}
      />
    </ProForm>
  );
}
```

---
