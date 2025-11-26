---
文档类型: 知识库文档
需求编号: DOC-2025-11-005
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# TypeScript 代码规范

**基于**: [Google TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html)
**适用范围**: AIBidComposer 项目的 React 前端
**技术栈**: TypeScript 5.x + React 18 + Ant Design Pro 6.x

---

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-26 | 1.0 | claude-sonnet-4-5 | 基于 Google TypeScript Style Guide 创建规范 |

---

## 1. 源文件基础

### 1.1 文件名

- **小写字母**
- **短横线分隔**（kebab-case）或**小驼峰**（camelCase）
- 组件文件使用 `.tsx` 扩展名
- 非组件文件使用 `.ts` 扩展名

```typescript
// ✅ 正确
user-service.ts
userService.ts
UserProfile.tsx       // React 组件
user-profile.tsx      // React 组件

// ❌ 错误
UserService.ts        // ❌ 非组件不要用大驼峰
user_service.ts       // ❌ 不要用下划线
```

### 1.2 文件编码

- **UTF-8** 编码
- **无 BOM**（Byte Order Mark）

### 1.3 文件组织

**React 组件文件结构**：
```typescript
// 1. 导入语句（分组）
import React, { useState, useEffect } from 'react';

import { Button, Form, Input, message } from 'antd';
import type { FormInstance } from 'antd';

import { getUserById, updateUser } from '@/services/user';
import type { User } from '@/types/user';

// 2. 类型定义
interface UserFormProps {
  userId: string;
  onSuccess?: () => void;
}

// 3. 组件定义
const UserForm: React.FC<UserFormProps> = ({ userId, onSuccess }) => {
  // 组件内容
};

// 4. 导出
export default UserForm;
```

---

## 2. 格式化规范

### 2.1 缩进

- **2个空格** 为一个缩进层级（前端约定）
- **禁止使用 Tab**

```typescript
// ✅ 正确：2空格缩进
function processUser(user: User): void {
  if (user.isActive) {
    console.log(user.name);
  }
}
```

### 2.2 行长度

- **每行最多 100 字符**
- 例外：长 URL、导入语句

### 2.3 分号

- **必须使用分号**

```typescript
// ✅ 正确
const name = 'John';
const age = 30;

// ❌ 错误
const name = 'John'  // ❌ 缺少分号
const age = 30       // ❌ 缺少分号
```

### 2.4 大括号

**K&R 风格**：
```typescript
// ✅ 正确
if (condition) {
  doSomething();
} else {
  doSomethingElse();
}

// ✅ 即使只有一行也要加大括号
if (condition) {
  return true;
}

// ❌ 错误：省略大括号
if (condition)
  return true;  // ❌ 容易引入bug

// ❌ 错误：大括号单独成行
if (condition)
{  // ❌ 不符合 K&R 风格
  doSomething();
}
```

### 2.5 空格

```typescript
// ✅ 正确：运算符两侧有空格
const sum = a + b;
if (x === y) {
  // ...
}

// ✅ 正确：逗号后有空格
const arr = [1, 2, 3];
const obj = { name: 'John', age: 30 };

// ✅ 正确：冒号在对象中的使用
{ key: 'value' }  // 冒号后有空格，前面没有

// ❌ 错误：多余的空格
const arr = [ 1, 2, 3 ];  // ❌ 括号内不要空格
const obj = { name:'John' };  // ❌ 冒号后要空格
```

### 2.6 尾随逗号

**必须使用尾随逗号**（多行时）：
```typescript
// ✅ 正确：多行时使用尾随逗号
const user = {
  name: 'John',
  age: 30,
  email: 'john@example.com',  // ✅ 尾随逗号
};

const items = [
  'item1',
  'item2',
  'item3',  // ✅ 尾随逗号
];

// ✅ 单行时不需要尾随逗号
const user = { name: 'John', age: 30 };
```

---

## 3. 命名规范

### 3.1 命名风格

| 类型 | 命名风格 | 示例 |
|------|---------|------|
| 文件名 | kebab-case / camelCase | `user-service.ts`, `userService.ts` |
| 类/接口/类型 | PascalCase | `User`, `UserService`, `UserFormProps` |
| 函数/方法 | camelCase | `getUserById`, `handleSubmit` |
| 变量/参数 | camelCase | `userName`, `userId` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `API_BASE_URL` |
| React 组件 | PascalCase | `UserForm`, `UserProfile` |
| 私有属性 | `_camelCase` (可选) | `_privateMethod` |

### 3.2 接口和类型

**接口命名**（不使用 `I` 前缀）：
```typescript
// ✅ 正确
interface User {
  id: string;
  name: string;
}

interface UserFormProps {
  userId: string;
  onSuccess?: () => void;
}

// ❌ 错误
interface IUser {  // ❌ 不要使用 I 前缀
  id: string;
}
```

**类型别名命名**：
```typescript
// ✅ 正确
type UserId = string;
type UserStatus = 'active' | 'inactive' | 'pending';
type UserCallback = (user: User) => void;

// Props 类型
type ButtonProps = {
  variant: 'primary' | 'secondary';
  onClick: () => void;
};
```

### 3.3 React 组件命名

```typescript
// ✅ 正确：组件名大驼峰
const UserProfile: React.FC<UserProfileProps> = (props) => {
  return <div>...</div>;
};

// ✅ 正确：Hooks 以 use 开头
const useUserData = (userId: string) => {
  const [user, setUser] = useState<User | null>(null);
  return { user, setUser };
};

// ❌ 错误
const userProfile = () => { };  // ❌ 组件名应该大驼峰
const UserHook = () => { };     // ❌ Hook 应该以 use 开头
```

### 3.4 常量

```typescript
// ✅ 正确：全大写+下划线
const MAX_RETRY_COUNT = 3;
const API_BASE_URL = 'https://api.example.com';
const DEFAULT_PAGE_SIZE = 20;

// ✅ 正确：枚举式对象
const UserStatus = {
  ACTIVE: 'active',
  INACTIVE: 'inactive',
  PENDING: 'pending',
} as const;

// ❌ 错误
const maxRetryCount = 3;  // ❌ 常量应该全大写
```

---

## 4. 类型系统

### 4.1 类型注解

**优先使用类型推断**：
```typescript
// ✅ 推荐：类型推断
const name = 'John';  // 推断为 string
const age = 30;       // 推断为 number

// ✅ 需要时使用类型注解
const user: User = getUserById('123');

// ❌ 不必要的类型注解
const name: string = 'John';  // ⚠️ 不必要，类型可以推断
```

**函数参数和返回值必须标注类型**：
```typescript
// ✅ 正确：参数和返回值都有类型
function getUserById(id: string): User | null {
  // ...
}

// ✅ 正确：箭头函数
const getUserById = (id: string): User | null => {
  // ...
};

// ❌ 错误：缺少类型注解
function getUserById(id) {  // ❌ 参数缺少类型
  // ...
}
```

### 4.2 接口 vs 类型别名

**优先使用接口**（可扩展）：
```typescript
// ✅ 推荐：使用 interface
interface User {
  id: string;
  name: string;
  email: string;
}

// ✅ 接口可以扩展
interface Employee extends User {
  department: string;
}

// ✅ 类型别名用于联合类型、元组等
type Status = 'active' | 'inactive';
type Point = [number, number];
type Callback = (data: string) => void;
```

### 4.3 联合类型和交叉类型

```typescript
// ✅ 联合类型（或）
type Result = User | Error;
type Status = 'success' | 'error' | 'loading';

// ✅ 交叉类型（与）
type Employee = User & {
  employeeId: string;
  department: string;
};

// ✅ 可辨识联合
type Response =
  | { status: 'success'; data: User }
  | { status: 'error'; error: string };

function handleResponse(response: Response) {
  if (response.status === 'success') {
    // TypeScript 知道这里是 success 分支
    console.log(response.data);
  } else {
    // TypeScript 知道这里是 error 分支
    console.error(response.error);
  }
}
```

### 4.4 泛型

```typescript
// ✅ 泛型函数
function first<T>(arr: T[]): T | undefined {
  return arr[0];
}

// ✅ 泛型接口
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 使用
const userResponse: ApiResponse<User> = {
  code: 200,
  message: 'Success',
  data: { id: '1', name: 'John', email: 'john@example.com' },
};

// ✅ 泛型约束
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}
```

### 4.5 类型断言

**尽量避免类型断言**：
```typescript
// ⚠️ 仅在必要时使用类型断言
const input = document.getElementById('myInput') as HTMLInputElement;

// ✅ 更好：类型守卫
function isHTMLInputElement(el: HTMLElement): el is HTMLInputElement {
  return el.tagName === 'INPUT';
}

const element = document.getElementById('myInput');
if (element && isHTMLInputElement(element)) {
  console.log(element.value);
}

// ❌ 避免：双重断言
const value = input as unknown as string;  // ❌ 危险
```

### 4.6 null 和 undefined

```typescript
// ✅ 使用 Optional 表示可能为 undefined
interface User {
  id: string;
  name: string;
  email?: string;  // email 可能为 undefined
}

// ✅ 使用 | null 表示可能为 null
function findUser(id: string): User | null {
  // 找不到返回 null
}

// ✅ 使用可选链和空值合并
const email = user?.email ?? 'no-email@example.com';

// ❌ 避免：混用 null 和 undefined
function getUser(): User | null | undefined {  // ❌ 不要混用
  // ...
}
```

---

## 5. React 编程实践

### 5.1 函数组件

**优先使用函数组件**：
```typescript
// ✅ 推荐：函数组件 + TypeScript
interface UserCardProps {
  user: User;
  onEdit?: (user: User) => void;
}

const UserCard: React.FC<UserCardProps> = ({ user, onEdit }) => {
  const handleEdit = () => {
    onEdit?.(user);
  };

  return (
    <div className="user-card">
      <h3>{user.name}</h3>
      <p>{user.email}</p>
      {onEdit && (
        <button onClick={handleEdit}>编辑</button>
      )}
    </div>
  );
};

export default UserCard;
```

### 5.2 Hooks 使用

```typescript
import { useState, useEffect, useCallback, useMemo } from 'react';

const UserProfile: React.FC<{ userId: string }> = ({ userId }) => {
  // ✅ useState 类型推断
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);

  // ✅ useEffect
  useEffect(() => {
    const fetchUser = async () => {
      setLoading(true);
      try {
        const data = await getUserById(userId);
        setUser(data);
      } catch (error) {
        console.error('加载用户失败', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [userId]);  // ✅ 依赖数组

  // ✅ useCallback 缓存函数
  const handleUpdate = useCallback(
    async (updates: Partial<User>) => {
      if (!user) return;

      try {
        const updated = await updateUser(user.id, updates);
        setUser(updated);
      } catch (error) {
        console.error('更新失败', error);
      }
    },
    [user],  // ✅ 依赖项
  );

  // ✅ useMemo 缓存计算结果
  const displayName = useMemo(
    () => user ? `${user.name} (${user.email})` : '',
    [user],
  );

  if (loading) return <div>加载中...</div>;
  if (!user) return <div>用户不存在</div>;

  return (
    <div>
      <h2>{displayName}</h2>
      {/* ... */}
    </div>
  );
};
```

### 5.3 自定义 Hook

```typescript
// ✅ 自定义 Hook（必须以 use 开头）
function useUserData(userId: string) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    let cancelled = false;

    const fetchUser = async () => {
      setLoading(true);
      setError(null);

      try {
        const data = await getUserById(userId);
        if (!cancelled) {
          setUser(data);
        }
      } catch (err) {
        if (!cancelled) {
          setError(err as Error);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    fetchUser();

    return () => {
      cancelled = true;
    };
  }, [userId]);

  return { user, loading, error };
}

// 使用
const UserProfile: React.FC<{ userId: string }> = ({ userId }) => {
  const { user, loading, error } = useUserData(userId);

  if (loading) return <div>加载中...</div>;
  if (error) return <div>错误: {error.message}</div>;
  if (!user) return null;

  return <div>{user.name}</div>;
};
```

### 5.4 事件处理

```typescript
// ✅ 正确：类型化的事件处理
const UserForm: React.FC = () => {
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    // ...
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    // ...
  };

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log('点击位置', event.clientX, event.clientY);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input onChange={handleInputChange} />
      <button onClick={handleClick}>提交</button>
    </form>
  );
};
```

### 5.5 Ref 使用

```typescript
import { useRef, useEffect } from 'react';

const InputFocus: React.FC = () => {
  // ✅ useRef 类型注解
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    // ✅ 使用前检查 ref.current
    inputRef.current?.focus();
  }, []);

  return <input ref={inputRef} />;
};
```

---

## 6. Ant Design Pro 实践

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

## 7. API 调用规范

### 7.1 使用 axios 或 umi-request

```typescript
import { request } from 'umi';

// ✅ 定义 API 响应类型
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// ✅ 定义服务函数
export async function getUserById(id: string): Promise<User> {
  const response = await request<ApiResponse<User>>(`/api/users/${id}`, {
    method: 'GET',
  });

  if (response.code !== 200) {
    throw new Error(response.message);
  }

  return response.data;
}

export async function createUser(user: Omit<User, 'id'>): Promise<User> {
  const response = await request<ApiResponse<User>>('/api/users', {
    method: 'POST',
    data: user,
  });

  if (response.code !== 200) {
    throw new Error(response.message);
  }

  return response.data;
}

export async function updateUser(
  id: string,
  updates: Partial<User>,
): Promise<User> {
  const response = await request<ApiResponse<User>>(`/api/users/${id}`, {
    method: 'PUT',
    data: updates,
  });

  if (response.code !== 200) {
    throw new Error(response.message);
  }

  return response.data;
}

export async function deleteUser(id: string): Promise<void> {
  const response = await request<ApiResponse<void>>(`/api/users/${id}`, {
    method: 'DELETE',
  });

  if (response.code !== 200) {
    throw new Error(response.message);
  }
}
```

### 7.2 错误处理

```typescript
import { message } from 'antd';

// ✅ 统一错误处理
const UserList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const data = await getUserList();
      setUsers(data);
    } catch (error) {
      if (error instanceof Error) {
        message.error(`加载失败: ${error.message}`);
      } else {
        message.error('加载失败');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  // ...
};
```

---

## 8. 项目特定补充规范

### 8.1 需求编号标注

```typescript
/**
 * 用户管理模块
 *
 * 需求编号: REQ-FRONT-001
 * 实现日期: 2025-11-26
 */

/**
 * 用户表单组件
 *
 * 需求编号: REQ-FRONT-001
 *
 * @param userId - 用户ID（编辑模式）
 * @param onSuccess - 成功回调
 */
const UserForm: React.FC<UserFormProps> = ({ userId, onSuccess }) => {
  // 需求编号: REQ-FRONT-001 - 用户创建和编辑功能
  const handleSubmit = async (values: UserFormValues) => {
    // ...
  };

  return <div>...</div>;
};
```

### 8.2 注释规范

```typescript
// ✅ 使用 JSDoc 注释
/**
 * 格式化用户显示名称
 *
 * @param user - 用户对象
 * @returns 格式化后的显示名称
 */
function formatUserDisplayName(user: User): string {
  return `${user.name} (${user.email})`;
}

// ✅ 单行注释解释复杂逻辑
const calculateScore = (items: Item[]): number => {
  // 计算加权平均分：质量(60%) + 速度(30%) + 完整性(10%)
  const qualityScore = items.reduce((sum, item) => sum + item.quality, 0);
  const speedScore = items.reduce((sum, item) => sum + item.speed, 0);
  const completenessScore = items.reduce((sum, item) => sum + item.completeness, 0);

  return qualityScore * 0.6 + speedScore * 0.3 + completenessScore * 0.1;
};
```

### 8.3 目录结构规范

```
src/
├── components/          # 公共组件
│   ├── UserCard/
│   │   ├── index.tsx
│   │   ├── index.less
│   │   └── types.ts
│   └── ...
├── pages/              # 页面组件
│   ├── User/
│   │   ├── List/
│   │   │   ├── index.tsx
│   │   │   └── index.less
│   │   ├── Detail/
│   │   └── index.tsx
│   └── ...
├── services/           # API 服务
│   ├── user.ts
│   └── ...
├── types/              # 类型定义
│   ├── user.ts
│   └── ...
├── utils/              # 工具函数
│   ├── format.ts
│   └── ...
├── hooks/              # 自定义 Hooks
│   ├── useUserData.ts
│   └── ...
└── app.tsx
```

---

## 9. 代码审查检查清单

提交代码前，请确保：

### 格式化
- [ ] 使用 2 空格缩进
- [ ] 每行不超过 100 字符
- [ ] 使用分号结束语句
- [ ] 大括号使用 K&R 风格

### 命名
- [ ] 组件名使用大驼峰（PascalCase）
- [ ] 函数/变量名使用小驼峰（camelCase）
- [ ] 常量使用全大写+下划线（UPPER_SNAKE_CASE）
- [ ] Hook 以 use 开头

### 类型
- [ ] 函数参数有类型注解
- [ ] 函数返回值有类型注解
- [ ] 组件 Props 有完整类型定义
- [ ] 避免使用 any 类型

### React
- [ ] 使用函数组件
- [ ] 正确使用 Hooks（依赖数组）
- [ ] 事件处理函数有正确的类型
- [ ] 避免不必要的重渲染

### Ant Design Pro
- [ ] ProTable 列定义有完整类型
- [ ] ProForm 表单值有类型定义
- [ ] 正确使用 ProComponents

### 项目特定
- [ ] 代码注释包含需求编号
- [ ] 使用中文注释解释业务逻辑
- [ ] API 调用有错误处理
- [ ] 组件有 loading 和 error 状态

---

## 10. 工具配置

### 10.1 ESLint 配置

`.eslintrc.js`:
```javascript
module.exports = {
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react/recommended',
    'plugin:react-hooks/recommended',
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2021,
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
    },
  },
  rules: {
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/explicit-function-return-type': 'off',
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    'react/react-in-jsx-scope': 'off',
    'react/prop-types': 'off',
  },
};
```

### 10.2 Prettier 配置

`.prettierrc.js`:
```javascript
module.exports = {
  printWidth: 100,
  tabWidth: 2,
  useTabs: false,
  semi: true,
  singleQuote: true,
  trailingComma: 'all',
  bracketSpacing: true,
  arrowParens: 'always',
};
```

### 10.3 TypeScript 配置

`tsconfig.json`:
```json
{
  "compilerOptions": {
    "target": "ES2021",
    "lib": ["ES2021", "DOM", "DOM.Iterable"],
    "jsx": "react-jsx",
    "module": "ESNext",
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "forceConsistentCasingInFileNames": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src"],
  "exclude": ["node_modules", "dist"]
}
```

---

## 参考资料

- [Google TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html)
- [React TypeScript Cheatsheet](https://react-typescript-cheatsheet.netlify.app/)
- [Ant Design Pro Documentation](https://pro.ant.design/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)

---

**最后更新**: 2025-11-26
**版本**: 1.0
