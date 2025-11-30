---
文档类型: 实现文档
需求编号: DOC-2025-11-001
创建日期: 2025-11-29
创建者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
最后更新: 2025-11-29
更新者: claude-sonnet-4-5 (claude-sonnet-4-5-20250929)
状态: 已批准
---

# AI标书智能创作平台 - 基于Specs的高级复用方案

> **复用来源**: .speckit规格文档
> **核心内容**: 嵌套模板、用户组织管理、AI Prompt管理、任务组织、国际化

## 📋 文档导航

本文档是功能复用实施指南的一部分，其他相关文档：

1. [00-功能复用实施指南总览.md](./00-功能复用实施指南总览.md) - 复用路线图和技术要求
2. [01-立即可复用的核心模块.md](./01-立即可复用的核心模块.md) - BlockNode、表单、模板
3. [02-需要改造的模块.md](./02-需要改造的模块.md) - 项目管理、AI助手改造
4. **03-基于Specs的高级复用方案.md**（本文档）- 嵌套模板、用户管理等
5. [04-新增模块与实施计划.md](./04-新增模块与实施计划.md) - 后端服务、分阶段计划
6. [05-检查清单与工具脚本.md](./05-检查清单与工具脚本.md) - 复用检查、迁移脚本
7. [06-问题解决与优化建议.md](./06-问题解决与优化建议.md) - 常见问题、性能优化


### 3补.1 嵌套模板系统实现（基于 spec-005）

#### 数据结构复用
```typescript
// 直接复用 ai-doc 的嵌套模板结构
interface DeliverableBlockTemplate {
  id: string;
  name: string;
  defaultContent: string;
  level: number;  // 最多5层
  children?: TemplateChildConstraint[];
}

interface TemplateChildConstraint {
  childTemplateId: string;
  isRequired: boolean;
  minCount: number;  // 默认 0
  maxCount: number;  // 默认 1
  orderIndex: number;
  supplementContent?: string;  // 父模板补充内容
}

// 标书文档的应用
interface BidDocumentTemplate extends DeliverableBlockTemplate {
  templateType: 'technical' | 'commercial' | 'qualification';
  industryCategory?: string;
  evaluationCriteria?: any;  // 评分标准关联
}
```

#### 树形编辑器组件
```tsx
// 复用 ai-doc 的树形模板编辑器
import { TemplateTreeEditor } from '@ai-doc/components';

function BidTemplateEditor() {
  return (
    <TemplateTreeEditor
      maxDepth={5}
      onDragEnd={handleReorder}
      renderNode={(node) => (
        <BidTemplateNode
          {...node}
          showConstraints
          showSupplementEditor
        />
      )}
      validation={{
        preventCircularRef: true,
        validateConstraints: true
      }}
    />
  );
}
```

### 3补.2 用户组织管理实现（基于 spec-003）

#### 复用用户邀请流程
```typescript
// 复用 ai-doc 的邀请激活工作流
interface InvitationFlow {
  // 第一步：管理员邀请
  async inviteUser(data: {
    email: string;
    role: 'Admin' | 'Member';
    companyId: string;
  }) {
    // 生成 token
    const token = await generateInvitationToken();
    // 发送邮件
    await sendInvitationEmail(email, token);
    // 保存邀请记录
    await saveInvitation({ ...data, token });
  }

  // 第二步：用户激活
  async activateAccount(token: string, password: string) {
    // 验证 token
    const invitation = await validateToken(token);
    // 创建账户
    const user = await createUser({
      ...invitation,
      password: hashPassword(password)
    });
    // 标记已激活
    await markTokenRedeemed(token);
    return user;
  }

  // 第三步：状态管理
  accountStatuses: {
    PENDING: 'pending_activation',
    ACTIVE: 'active',
    INACTIVE: 'inactive',
    SUSPENDED: 'suspended'
  }
}
```

#### 组织级权限管理
```typescript
// 复用组织隔离模式
class OrganizationContext {
  // 数据隔离
  @RequireOrganization()
  async getProjects(organizationId: string) {
    return this.projectRepo.find({
      where: { organizationId }
    });
  }

  // 级联状态控制
  async deactivateOrganization(orgId: string) {
    // 禁用组织
    await this.orgRepo.update(orgId, { status: 'inactive' });
    // 级联禁用用户
    await this.userRepo.update(
      { organizationId: orgId },
      { status: 'inactive' }
    );
    // 撤销活跃会话
    await this.sessionService.revokeOrgSessions(orgId);
  }
}
```

### 3补.3 AI Prompt 管理实现（基于 spec-008/011）

#### 三层 Prompt 体系
```typescript
// 1. 模板层 Prompt 配置
interface TemplateAIConfig {
  aiDocStyleGuide: string;      // 文档风格指南
  aiOutlineAdjustRule: string;  // 大纲调整规则
  aiSectionWriteConfig: {        // 章节写作配置
    tone: 'formal' | 'professional' | 'technical';
    length: 'brief' | 'standard' | 'detailed';
    examples?: string[];
  };
}

// 2. 实例层 Prompt 继承
class InstanceAIAssistant {
  constructor(
    private templateConfig: TemplateAIConfig,
    private instanceOverrides?: Partial<TemplateAIConfig>
  ) {}

  getPrompt(section: string): string {
    // 合并模板和实例配置
    const config = { ...this.templateConfig, ...this.instanceOverrides };

    return `
      风格指南：${config.aiDocStyleGuide}
      章节：${section}
      写作要求：${JSON.stringify(config.aiSectionWriteConfig)}
    `;
  }

  // 使用时机过滤
  filterByUsageTiming(assistants: Assistant[], timing: string) {
    return assistants.filter(a => a.usageTiming.includes(timing));
  }
}

// 3. 章节层自动化
interface SectionAutomation {
  trigger: 'onSave' | 'onDemand' | 'scheduled';
  operations: Array<{
    type: 'optimize' | 'expand' | 'summarize';
    aiModel: 'gpt-4' | 'claude-3';
    includeContext: boolean;
  }>;
}
```

#### Prompt 版本管理
```typescript
// 复用 ai-doc 的 Prompt 版本控制
class PromptVersionControl {
  // 保存历史
  async savePromptVersion(prompt: {
    content: string;
    variables: string[];
    performance: {
      tokenUsage: number;
      responseQuality: number;
    };
  }) {
    return this.versionRepo.save({
      ...prompt,
      version: await this.getNextVersion(),
      timestamp: new Date()
    });
  }

  // 回滚机制
  async rollbackPrompt(templateId: string, version: number) {
    const historicalPrompt = await this.versionRepo.findOne({
      templateId,
      version
    });

    await this.applyPrompt(templateId, historicalPrompt);
    return historicalPrompt;
  }

  // A/B 测试
  async comparePrompts(promptA: string, promptB: string) {
    const [resultA, resultB] = await Promise.all([
      this.testPrompt(promptA),
      this.testPrompt(promptB)
    ]);

    return {
      winner: resultA.score > resultB.score ? 'A' : 'B',
      metrics: { A: resultA, B: resultB }
    };
  }
}
```

### 3补.4 任务组织模式实现（基于 tasks.md）

#### 分阶段执行策略
```typescript
// 复用 ai-doc 的任务组织模式
interface TaskPhases {
  // Phase 1: 基础设施
  setup: Task[] = [
    { id: 'T001', parallel: true, description: '搭建前端框架' },
    { id: 'T002', parallel: true, description: '配置测试环境' },
    { id: 'T003', parallel: false, description: '主题配置' }
  ];

  // Phase 2: 核心功能
  foundational: Task[] = [
    { id: 'T010', parallel: false, description: '元数据加载' },
    { id: 'T011', parallel: false, description: '数据模型' },
    { id: 'T012', parallel: true, description: 'API 客户端' },
    { id: 'T013', parallel: true, description: 'AI 客户端' }
  ];

  // Phase 3-5: 用户故事
  userStories: {
    templateArchitect: Task[];  // 模板管理员
    projectLead: Task[];         // 项目负责人
    bidDesigner: Task[];         // 标书设计师
  };

  // Phase 6: 优化
  crossCutting: Task[] = [
    { id: 'T401', parallel: true, description: '性能优化' },
    { id: 'T402', parallel: true, description: '安全加固' }
  ];
}

// 任务执行器
class TaskExecutor {
  async executePhase(phase: Task[], options: {
    parallel?: boolean;
    onProgress?: (task: Task) => void;
  }) {
    if (options.parallel) {
      // 并行执行标记为 [P] 的任务
      const parallelTasks = phase.filter(t => t.parallel);
      await Promise.all(parallelTasks.map(t => this.runTask(t)));
    } else {
      // 串行执行
      for (const task of phase) {
        await this.runTask(task);
        options.onProgress?.(task);
      }
    }
  }
}
```

### 3补.5 国际化架构实现（基于 spec-002）

#### 消息目录管理
```typescript
// 复用 ai-doc 的 i18n 架构
interface MessageCatalog {
  'zh-CN': Record<string, string>;
  'en-US': Record<string, string>;
  'ja-JP': Record<string, string>;
}

// 语言切换组件
function LanguageSelector() {
  const { locale, setLocale } = useI18n();

  return (
    <Select
      value={locale}
      onChange={setLocale}
      options={[
        { value: 'zh-CN', label: '简体中文' },
        { value: 'en-US', label: 'English' },
        { value: 'ja-JP', label: '日本語' }
      ]}
    />
  );
}

// 持久化用户偏好
class LocalePreference {
  async saveUserLocale(userId: string, locale: string) {
    await this.userRepo.update(userId, { preferredLocale: locale });
    // 更新缓存
    await this.cache.set(`user:${userId}:locale`, locale);
  }

  async getUserLocale(userId: string): string {
    // 优先缓存
    const cached = await this.cache.get(`user:${userId}:locale`);
    if (cached) return cached;

    // 查询数据库
    const user = await this.userRepo.findOne(userId);
    return user?.preferredLocale || 'zh-CN';
  }
}
```

## 四、新增模块开发指南

## 🔗 相关文档

- **功能复用总览**: [00-功能复用实施指南总览.md](./00-功能复用实施指南总览.md)
- **立即可复用模块**: [01-立即可复用的核心模块.md](./01-立即可复用的核心模块.md)
- **实施计划**: [04-新增模块与实施计划.md](./04-新增模块与实施计划.md)

## 修改历史

| 日期 | 版本 | 修改者 | 修改内容概要 |
|------|------|--------|-------------|
| 2025-11-29 | 1.0 | claude-sonnet-4-5 (claude-sonnet-4-5-20250929) | 从ai-doc-功能复用实施指南.md拆分创建 |

---

**文档版本**: v1.0
**创建时间**: 2025年11月29日
**文档状态**: ✅ 已批准
