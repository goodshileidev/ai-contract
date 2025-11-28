#!/bin/bash
# ============================================================================
# AIBidComposer - 服务验证脚本
# ============================================================================
# 用途：验证所有Docker服务正常启动并可访问
#
# 使用方法：
#   ./scripts/verify-services.sh              # 验证开发环境
#   ./scripts/verify-services.sh --prod       # 验证生产环境

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 默认环境
ENVIRONMENT="dev"
COMPOSE_FILE="docker-compose.yml"

# 解析参数
if [[ "$1" == "--prod" ]]; then
    ENVIRONMENT="prod"
    COMPOSE_FILE="docker-compose.prod.yml"
fi

print_info() {
    echo -e "${BLUE}ℹ ${NC}$1"
}

print_success() {
    echo -e "${GREEN}✓ ${NC}$1"
}

print_warning() {
    echo -e "${YELLOW}⚠ ${NC}$1"
}

print_error() {
    echo -e "${RED}✗ ${NC}$1"
}

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

# 验证结果统计
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

increment_total() {
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
}

increment_passed() {
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    increment_total
}

increment_failed() {
    FAILED_CHECKS=$((FAILED_CHECKS + 1))
    increment_total
}

# 1. 检查Docker是否运行
check_docker() {
    print_header "1. 检查Docker环境"

    if ! docker info > /dev/null 2>&1; then
        print_error "Docker未运行"
        increment_failed
        exit 1
    fi

    print_success "Docker正在运行"
    increment_passed

    local version=$(docker --version)
    print_info "Docker版本: $version"
}

# 2. 检查Docker Compose
check_docker_compose() {
    print_header "2. 检查Docker Compose"

    if ! command -v docker-compose &> /dev/null; then
        print_error "docker-compose未安装"
        increment_failed
        exit 1
    fi

    print_success "docker-compose已安装"
    increment_passed

    local version=$(docker-compose --version)
    print_info "版本: $version"
}

# 3. 检查环境变量
check_env_file() {
    print_header "3. 检查环境配置"

    if [ ! -f .env ]; then
        print_error ".env文件不存在"
        print_info "请从.env.example复制并配置"
        increment_failed
        return 1
    fi

    print_success ".env文件存在"
    increment_passed

    # 检查关键环境变量
    source .env

    local required_vars=(
        "POSTGRES_PASSWORD"
        "REDIS_PASSWORD"
        "OPENAI_API_KEY"
    )

    local missing=0
    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            print_warning "缺少环境变量: $var"
            missing=$((missing + 1))
        fi
    done

    if [ $missing -eq 0 ]; then
        print_success "所有关键环境变量已配置"
    else
        print_warning "有 $missing 个环境变量未配置"
    fi
}

# 4. 检查容器运行状态
check_containers_running() {
    print_header "4. 检查容器运行状态"

    local containers=$(docker-compose -f $COMPOSE_FILE ps -q)

    if [ -z "$containers" ]; then
        print_error "没有运行的容器"
        print_info "请先运行: docker-compose up -d"
        increment_failed
        return 1
    fi

    local running_count=$(docker-compose -f $COMPOSE_FILE ps | grep -c "Up" || true)
    print_success "检测到 $running_count 个运行中的容器"
    increment_passed

    # 显示容器状态
    echo ""
    docker-compose -f $COMPOSE_FILE ps
    echo ""
}

# 5. 检查容器健康状态
check_containers_health() {
    print_header "5. 检查容器健康状态"

    local services=(
        "postgres"
        "elasticsearch"
        "redis"
        "rabbitmq"
        "minio"
    )

    for service in "${services[@]}"; do
        local health=$(docker-compose -f $COMPOSE_FILE ps $service | grep "(healthy)" || true)

        if [ -n "$health" ]; then
            print_success "$service: healthy"
            increment_passed
        else
            local status=$(docker-compose -f $COMPOSE_FILE ps $service | tail -n 1 | awk '{print $4}')
            if [[ "$status" == *"Up"* ]]; then
                print_warning "$service: running (no health check)"
                increment_passed
            else
                print_error "$service: not healthy"
                increment_failed
            fi
        fi
    done
}

# 6. 检查PostgreSQL连接
check_postgres() {
    print_header "6. 检查PostgreSQL数据库"

    if docker-compose -f $COMPOSE_FILE exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
        print_success "PostgreSQL可连接"
        increment_passed
    else
        print_error "PostgreSQL不可连接"
        increment_failed
        return 1
    fi

    # 检查数据库是否存在
    local db_exists=$(docker-compose -f $COMPOSE_FILE exec -T postgres psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='aibidcomposer'" 2>/dev/null || echo "")

    if [ "$db_exists" = "1" ]; then
        print_success "数据库 'aibidcomposer' 已创建"
        increment_passed
    else
        print_warning "数据库 'aibidcomposer' 不存在"
        increment_failed
    fi

    # 检查扩展
    local extensions=$(docker-compose -f $COMPOSE_FILE exec -T postgres psql -U postgres -d aibidcomposer -tAc "SELECT extname FROM pg_extension WHERE extname IN ('uuid-ossp', 'pg_trgm', 'btree_gin')" 2>/dev/null || echo "")

    if [[ "$extensions" == *"uuid-ossp"* ]] && [[ "$extensions" == *"pg_trgm"* ]]; then
        print_success "PostgreSQL扩展已安装"
        increment_passed
    else
        print_warning "部分PostgreSQL扩展未安装"
        increment_failed
    fi
}

# 7. 检查Elasticsearch
check_elasticsearch() {
    print_header "7. 检查Elasticsearch"

    local max_attempts=30
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        if curl -s -u elastic:${ELASTICSEARCH_PASSWORD:-elastic} http://localhost:9200/_cluster/health > /dev/null 2>&1; then
            print_success "Elasticsearch可访问"
            increment_passed

            # 获取集群状态
            local cluster_status=$(curl -s -u elastic:${ELASTICSEARCH_PASSWORD:-elastic} http://localhost:9200/_cluster/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
            print_info "集群状态: $cluster_status"

            if [ "$cluster_status" = "green" ] || [ "$cluster_status" = "yellow" ]; then
                print_success "集群状态正常"
                increment_passed
            else
                print_warning "集群状态: $cluster_status"
                increment_failed
            fi

            return 0
        fi

        attempt=$((attempt + 1))
        sleep 1
    done

    print_error "Elasticsearch不可访问（超时）"
    increment_failed
}

# 8. 检查Redis
check_redis() {
    print_header "8. 检查Redis"

    if docker-compose -f $COMPOSE_FILE exec -T redis redis-cli -a ${REDIS_PASSWORD:-redis} ping 2>/dev/null | grep -q "PONG"; then
        print_success "Redis可连接"
        increment_passed

        # 获取Redis信息
        local redis_version=$(docker-compose -f $COMPOSE_FILE exec -T redis redis-cli -a ${REDIS_PASSWORD:-redis} INFO SERVER 2>/dev/null | grep "redis_version" | cut -d':' -f2 | tr -d '\r')
        print_info "Redis版本: $redis_version"

        # 测试读写
        docker-compose -f $COMPOSE_FILE exec -T redis redis-cli -a ${REDIS_PASSWORD:-redis} SET test_key "test_value" > /dev/null 2>&1
        local test_value=$(docker-compose -f $COMPOSE_FILE exec -T redis redis-cli -a ${REDIS_PASSWORD:-redis} GET test_key 2>/dev/null | tr -d '\r')
        docker-compose -f $COMPOSE_FILE exec -T redis redis-cli -a ${REDIS_PASSWORD:-redis} DEL test_key > /dev/null 2>&1

        if [ "$test_value" = "test_value" ]; then
            print_success "Redis读写正常"
            increment_passed
        else
            print_error "Redis读写失败"
            increment_failed
        fi
    else
        print_error "Redis不可连接"
        increment_failed
    fi
}

# 9. 检查RabbitMQ
check_rabbitmq() {
    print_header "9. 检查RabbitMQ"

    local max_attempts=30
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        if curl -s -u ${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq} http://localhost:15672/api/overview > /dev/null 2>&1; then
            print_success "RabbitMQ Management UI可访问"
            increment_passed

            local rabbitmq_version=$(curl -s -u ${RABBITMQ_USER:-rabbitmq}:${RABBITMQ_PASSWORD:-rabbitmq} http://localhost:15672/api/overview | grep -o '"rabbitmq_version":"[^"]*"' | cut -d'"' -f4)
            print_info "RabbitMQ版本: $rabbitmq_version"

            return 0
        fi

        attempt=$((attempt + 1))
        sleep 1
    done

    print_error "RabbitMQ Management UI不可访问（超时）"
    increment_failed
}

# 10. 检查MinIO
check_minio() {
    print_header "10. 检查MinIO"

    if curl -s http://localhost:9000/minio/health/live > /dev/null 2>&1; then
        print_success "MinIO可访问"
        increment_passed

        if curl -s http://localhost:9001 > /dev/null 2>&1; then
            print_success "MinIO Console可访问"
            print_info "Console地址: http://localhost:9001"
            increment_passed
        else
            print_warning "MinIO Console不可访问"
            increment_failed
        fi
    else
        print_error "MinIO不可访问"
        increment_failed
    fi
}

# 11. 检查Java后端服务
check_backend_java() {
    print_header "11. 检查Java Spring Boot服务"

    local max_attempts=60
    local attempt=0

    print_info "等待Java服务启动（最多60秒）..."

    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "Java服务可访问"
            increment_passed

            local health_status=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
            print_info "健康状态: $health_status"

            if [ "$health_status" = "UP" ]; then
                print_success "Java服务健康状态正常"
                increment_passed
            else
                print_warning "Java服务健康状态: $health_status"
                increment_failed
            fi

            print_info "Swagger UI: http://localhost:8080/swagger-ui.html"
            return 0
        fi

        attempt=$((attempt + 1))
        sleep 1
    done

    print_error "Java服务不可访问（超时）"
    print_info "请检查日志: docker-compose logs backend-java"
    increment_failed
}

# 12. 检查Python AI服务
check_backend_python() {
    print_header "12. 检查Python FastAPI服务"

    local max_attempts=60
    local attempt=0

    print_info "等待Python服务启动（最多60秒）..."

    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:8001/health > /dev/null 2>&1; then
            print_success "Python AI服务可访问"
            increment_passed

            local health_response=$(curl -s http://localhost:8001/health)
            print_info "健康检查响应: $health_response"

            print_success "Python AI服务正常运行"
            print_info "API文档: http://localhost:8001/docs"
            print_info "ReDoc: http://localhost:8001/redoc"
            increment_passed

            return 0
        fi

        attempt=$((attempt + 1))
        sleep 1
    done

    print_error "Python AI服务不可访问（超时）"
    print_info "请检查日志: docker-compose logs backend-python"
    increment_failed
}

# 13. 检查前端服务
check_frontend() {
    print_header "13. 检查React前端服务"

    local max_attempts=60
    local attempt=0

    print_info "等待前端服务启动（最多60秒）..."

    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:5173 > /dev/null 2>&1; then
            print_success "前端服务可访问"
            print_info "前端地址: http://localhost:5173"
            increment_passed
            return 0
        fi

        attempt=$((attempt + 1))
        sleep 1
    done

    print_error "前端服务不可访问（超时）"
    print_info "请检查日志: docker-compose logs frontend"
    increment_failed
}

# 14. 检查Celery Worker
check_celery_worker() {
    print_header "14. 检查Celery Worker"

    local worker_status=$(docker-compose -f $COMPOSE_FILE ps ai-worker | grep "Up" || true)

    if [ -n "$worker_status" ]; then
        print_success "Celery Worker运行中"
        increment_passed

        # 检查Worker日志中是否有错误
        local recent_errors=$(docker-compose -f $COMPOSE_FILE logs --tail=50 ai-worker 2>/dev/null | grep -i "error" || true)

        if [ -z "$recent_errors" ]; then
            print_success "Celery Worker无错误"
            increment_passed
        else
            print_warning "Celery Worker日志中发现错误"
            increment_failed
        fi
    else
        print_error "Celery Worker未运行"
        increment_failed
    fi
}

# 15. 服务间通信测试
check_service_communication() {
    print_header "15. 检查服务间通信"

    print_info "测试Java服务访问PostgreSQL..."
    local java_db_test=$(docker-compose -f $COMPOSE_FILE exec -T backend-java curl -s http://localhost:8080/actuator/health/db 2>/dev/null || echo "")

    if [[ "$java_db_test" == *"UP"* ]]; then
        print_success "Java → PostgreSQL 通信正常"
        increment_passed
    else
        print_warning "Java → PostgreSQL 通信异常"
        increment_failed
    fi

    print_info "测试Java服务访问Redis..."
    local java_redis_test=$(docker-compose -f $COMPOSE_FILE exec -T backend-java curl -s http://localhost:8080/actuator/health/redis 2>/dev/null || echo "")

    if [[ "$java_redis_test" == *"UP"* ]]; then
        print_success "Java → Redis 通信正常"
        increment_passed
    else
        print_warning "Java → Redis 通信异常"
        increment_failed
    fi
}

# 显示验证摘要
show_summary() {
    print_header "验证摘要"

    echo -e "${BLUE}总检查项: ${NC}$TOTAL_CHECKS"
    echo -e "${GREEN}通过: ${NC}$PASSED_CHECKS"
    echo -e "${RED}失败: ${NC}$FAILED_CHECKS"

    local pass_rate=0
    if [ $TOTAL_CHECKS -gt 0 ]; then
        pass_rate=$((PASSED_CHECKS * 100 / TOTAL_CHECKS))
    fi

    echo -e "${BLUE}通过率: ${NC}${pass_rate}%"

    echo ""

    if [ $FAILED_CHECKS -eq 0 ]; then
        print_success "所有检查均通过！系统运行正常。"
        return 0
    else
        print_warning "有 $FAILED_CHECKS 项检查失败，请查看详细信息。"
        return 1
    fi
}

# 显示访问信息
show_access_info() {
    print_header "服务访问信息"

    echo -e "${GREEN}应用服务:${NC}"
    echo "  前端:          http://localhost:5173"
    echo "  Java API:      http://localhost:8080"
    echo "  Python AI:     http://localhost:8001"
    echo ""

    echo -e "${GREEN}API文档:${NC}"
    echo "  Swagger UI:    http://localhost:8080/swagger-ui.html"
    echo "  FastAPI Docs:  http://localhost:8001/docs"
    echo "  ReDoc:         http://localhost:8001/redoc"
    echo ""

    echo -e "${GREEN}数据服务:${NC}"
    echo "  PostgreSQL:    localhost:5432 (user: postgres)"
    echo "  Elasticsearch: http://localhost:9200"
    echo "  Redis:         localhost:6379"
    echo "  RabbitMQ Web:  http://localhost:15672 (guest/guest)"
    echo "  MinIO Console: http://localhost:9001 (minioadmin/minioadmin)"
    echo ""

    echo -e "${YELLOW}提示:${NC}"
    echo "  查看日志: ${BLUE}docker-compose logs -f [service_name]${NC}"
    echo "  重启服务: ${BLUE}docker-compose restart [service_name]${NC}"
    echo "  停止服务: ${BLUE}docker-compose stop${NC}"
    echo ""
}

# 主函数
main() {
    print_header "AIBidComposer 服务验证 - $ENVIRONMENT 环境"

    # 执行所有检查
    check_docker
    check_docker_compose
    check_env_file
    check_containers_running
    check_containers_health
    check_postgres
    check_elasticsearch
    check_redis
    check_rabbitmq
    check_minio
    check_backend_java
    check_backend_python

    if [ "$ENVIRONMENT" = "dev" ]; then
        check_frontend
    fi

    check_celery_worker
    check_service_communication

    # 显示摘要
    show_summary
    local summary_result=$?

    # 显示访问信息
    show_access_info

    return $summary_result
}

# 运行主函数
main
