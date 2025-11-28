-- ============================================================================
-- AIBidComposer - PostgreSQL 初始化脚本
-- ============================================================================
-- 此脚本在 PostgreSQL 容器首次启动时自动执行
-- 用于创建数据库、扩展和初始配置

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- 连接到 aibidcomposer 数据库
\c aibidcomposer;

-- ============================================================================
-- 安装扩展
-- ============================================================================

-- UUID 生成扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
COMMENT ON EXTENSION "uuid-ossp" IS '用于生成 UUID 主键';

-- 模糊搜索扩展（用于用户名、项目名等搜索）
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
COMMENT ON EXTENSION "pg_trgm" IS '用于模糊搜索和相似度匹配';

-- GIN 索引扩展（用于 JSONB 和数组字段）
CREATE EXTENSION IF NOT EXISTS "btree_gin";
COMMENT ON EXTENSION "btree_gin" IS '用于 JSONB 和数组字段的索引优化';

-- 时序数据扩展（用于日志和审计表）
-- 注意：需要安装 TimescaleDB 扩展，如果没有安装则注释掉
-- CREATE EXTENSION IF NOT EXISTS "timescaledb";
-- COMMENT ON EXTENSION "timescaledb" IS '用于时序数据优化';

-- ============================================================================
-- 设置时区
-- ============================================================================
SET TIME ZONE 'Asia/Shanghai';
ALTER DATABASE aibidcomposer SET timezone TO 'Asia/Shanghai';

-- ============================================================================
-- 创建数据库用户（如果需要独立用户）
-- ============================================================================
-- 创建应用专用用户（可选）
-- CREATE USER aibidcomposer_app WITH PASSWORD 'your_app_password';

-- 授予权限
-- GRANT CONNECT ON DATABASE aibidcomposer TO aibidcomposer_app;
-- GRANT USAGE ON SCHEMA public TO aibidcomposer_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO aibidcomposer_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO aibidcomposer_app;

-- ============================================================================
-- 创建自定义类型和枚举（示例）
-- ============================================================================

-- 用户状态枚举
DO $$ BEGIN
    CREATE TYPE user_status AS ENUM ('active', 'inactive', 'suspended');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;
COMMENT ON TYPE user_status IS '用户状态：active-活跃, inactive-未激活, suspended-已暂停';

-- 项目状态枚举
DO $$ BEGIN
    CREATE TYPE project_status AS ENUM (
        'draft',
        'in_progress',
        'review',
        'submitted',
        'won',
        'lost',
        'archived'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;
COMMENT ON TYPE project_status IS '项目状态';

-- 优先级枚举
DO $$ BEGIN
    CREATE TYPE priority_level AS ENUM ('low', 'medium', 'high', 'urgent');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;
COMMENT ON TYPE priority_level IS '优先级';

-- ============================================================================
-- 创建通用函数
-- ============================================================================

-- 自动更新 updated_at 字段的函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION update_updated_at_column() IS '自动更新 updated_at 时间戳';

-- 软删除查询函数（示例）
CREATE OR REPLACE FUNCTION is_not_deleted()
RETURNS BOOLEAN AS $$
BEGIN
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- ============================================================================
-- 创建视图（用于简化查询）
-- ============================================================================

-- 注意：视图将在表创建后通过 Flyway 迁移脚本创建
-- 这里仅作示例

-- 活跃用户视图
-- CREATE OR REPLACE VIEW active_users AS
-- SELECT * FROM users WHERE deleted_at IS NULL AND status = 'active';

-- 活跃项目视图
-- CREATE OR REPLACE VIEW active_projects AS
-- SELECT * FROM projects WHERE deleted_at IS NULL;

-- ============================================================================
-- 性能优化配置
-- ============================================================================

-- 设置默认统计目标（用于查询优化器）
ALTER DATABASE aibidcomposer SET default_statistics_target = 100;

-- 启用并行查询
ALTER DATABASE aibidcomposer SET max_parallel_workers_per_gather = 4;

-- 设置工作内存
ALTER DATABASE aibidcomposer SET work_mem = '16MB';

-- 设置维护工作内存
ALTER DATABASE aibidcomposer SET maintenance_work_mem = '256MB';

-- 设置有效缓存大小
ALTER DATABASE aibidcomposer SET effective_cache_size = '4GB';

-- ============================================================================
-- 日志配置
-- ============================================================================

-- 记录慢查询（超过 1 秒）
ALTER DATABASE aibidcomposer SET log_min_duration_statement = 1000;

-- 记录检查点信息
ALTER DATABASE aibidcomposer SET log_checkpoints = on;

-- 记录连接信息
ALTER DATABASE aibidcomposer SET log_connections = on;
ALTER DATABASE aibidcomposer SET log_disconnections = on;

-- ============================================================================
-- 完成初始化
-- ============================================================================

-- 输出完成信息
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'AIBidComposer 数据库初始化完成！';
    RAISE NOTICE '数据库: aibidcomposer';
    RAISE NOTICE '扩展: uuid-ossp, pg_trgm, btree_gin';
    RAISE NOTICE '时区: Asia/Shanghai';
    RAISE NOTICE '========================================';
END $$;

-- 显示已安装的扩展
SELECT extname, extversion FROM pg_extension ORDER BY extname;

-- 显示数据库配置
SELECT name, setting FROM pg_settings
WHERE name IN (
    'timezone',
    'client_encoding',
    'default_statistics_target',
    'work_mem',
    'maintenance_work_mem',
    'effective_cache_size'
)
ORDER BY name;
