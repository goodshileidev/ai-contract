---
文档类型: 知识库文档
需求编号: DOC-2025-11-005
创建日期: 2025-11-26
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-26
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# TypeScript 代码规范 - 5. React 编程实践

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
