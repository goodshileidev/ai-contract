---
文档类型: 知识库文档
需求编号: DOC-2025-11-005
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# TypeScript 代码规范 - 6. Ant Design Pro 实践

### 6.1 ProTable 使用

```typescript
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';

interface UserTableItem {
  id: string;
  name: string;
  email: string;
  status: 'active' | 'inactive';
  createdAt: string;
}

const UserTable: React.FC = () => {
  const actionRef = useRef<ActionType>();

  // ✅ 定义列配置
  const columns: ProColumns<UserTableItem>[] = [
    {
      title: '用户名',
      dataIndex: 'name',
      key: 'name',
      copyable: true,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      copyable: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      valueEnum: {
        active: { text: '活跃', status: 'Success' },
        inactive: { text: '未激活', status: 'Default' },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      valueType: 'dateTime',
    },
    {
      title: '操作',
      key: 'action',
      valueType: 'option',
      render: (_, record) => [
        <a key="edit" onClick={() => handleEdit(record)}>
          编辑
        </a>,
        <a key="delete" onClick={() => handleDelete(record.id)}>
          删除
        </a>,
      ],
    },
  ];

  const handleEdit = (record: UserTableItem) => {
    // 编辑逻辑
  };

  const handleDelete = (id: string) => {
    // 删除逻辑
  };

  return (
    <ProTable<UserTableItem>
      columns={columns}
      actionRef={actionRef}
      request={async (params) => {
        // ✅ 请求数据
        const response = await fetchUsers(params);
        return {
          data: response.data,
          success: true,
          total: response.total,
        };
      }}
      rowKey="id"
      pagination={{
        defaultPageSize: 10,
        showSizeChanger: true,
      }}
      search={{
        labelWidth: 'auto',
      }}
      dateFormatter="string"
      headerTitle="用户列表"
    />
  );
};
```

### 6.2 ProForm 使用

```typescript
import { ProForm, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import type { ProFormInstance } from '@ant-design/pro-components';

interface UserFormValues {
  name: string;
  email: string;
  department: string;
}

const UserForm: React.FC<{ userId?: string }> = ({ userId }) => {
  const formRef = useRef<ProFormInstance>();

  const handleFinish = async (values: UserFormValues) => {
    try {
      if (userId) {
        await updateUser(userId, values);
        message.success('更新成功');
      } else {
        await createUser(values);
        message.success('创建成功');
      }
      return true;
    } catch (error) {
      message.error('操作失败');
      return false;
    }
  };

  return (
    <ProForm<UserFormValues>
      formRef={formRef}
      onFinish={handleFinish}
      initialValues={{
        name: '',
        email: '',
        department: '',
      }}
    >
      <ProFormText
        name="name"
        label="用户名"
        placeholder="请输入用户名"
        rules={[{ required: true, message: '请输入用户名' }]}
      />
      <ProFormText
        name="email"
        label="邮箱"
        placeholder="请输入邮箱"
        rules={[
          { required: true, message: '请输入邮箱' },
          { type: 'email', message: '邮箱格式不正确' },
        ]}
      />
      <ProFormSelect
        name="department"
        label="部门"
        placeholder="请选择部门"
        options={[
          { label: '技术部', value: 'tech' },
          { label: '产品部', value: 'product' },
          { label: '运营部', value: 'operations' },
        ]}
        rules={[{ required: true, message: '请选择部门' }]}
      />
    </ProForm>
  );
};
```

---
