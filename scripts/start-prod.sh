#!/bin/bash
# ============================================================================
# AIBidComposer - 生产环境启动脚本
# ============================================================================
# 用途：一键启动生产环境的所有服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# 检查生产环境变量
check_production_env() {
    print_info "检查生产环境配置..."

    local required_vars=(
        "POSTGRES_PASSWORD"
        "ELASTICSEARCH_PASSWORD"
        "REDIS_PASSWORD"
        "RABBITMQ_PASSWORD"
        "MINIO_ROOT_PASSWORD"
        "JWT_SECRET"
        "SECRET_KEY"
        "OPENAI_API_KEY"
    )

    local missing_vars=()

    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            missing_vars+=("$var")
        fi
    done

    if [ ${#missing_vars[@]} -ne 0 ]; then
        print_error "缺少必需的环境变量:"
        for var in "${missing_vars[@]}"; do
            echo "  - $var"
        done
        print_error "请在 .env 文件中配置这些变量"
        exit 1
    fi

    # 检查密码强度
    if [ ${#POSTGRES_PASSWORD} -lt 16 ]; then
        print_error "POSTGRES_PASSWORD 长度不足 16 位"
        exit 1
    fi

    if [ ${#JWT_SECRET} -lt 32 ]; then
        print_error "JWT_SECRET 长度不足 32 位"
        exit 1
    fi

    print_success "环境变量配置检查通过"
}

# 检查 Docker
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker 未运行"
        exit 1
    fi
    print_success "Docker 正在运行"
}

# 构建镜像
build_images() {
    print_info "构建生产镜像..."

    local version=${VERSION:-$(git rev-parse --short HEAD)}
    export VERSION=$version

    print_info "镜像版本: $version"

    docker-compose -f docker-compose.prod.yml build \
        --no-cache \
        --parallel

    print_success "镜像构建完成"
}

# 停止服务
stop_services() {
    print_info "停止现有服务..."
    docker-compose -f docker-compose.prod.yml down
}

# 启动服务
start_services() {
    print_info "启动生产服务..."
    docker-compose -f docker-compose.prod.yml up -d
}

# 等待服务健康
wait_for_services() {
    print_info "等待服务启动..."

    local max_attempts=120
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        local healthy=$(docker-compose -f docker-compose.prod.yml ps | grep -c "(healthy)" || true)
        local total=$(docker-compose -f docker-compose.prod.yml ps | grep -c "Up" || true)

        echo -n "健康服务: $healthy/$total"

        if [ "$healthy" -ge 5 ]; then
            echo ""
            print_success "服务启动成功"
            return 0
        fi

        attempt=$((attempt + 1))
        echo -n " ."
        sleep 3
    done

    print_error "服务启动超时"
    docker-compose -f docker-compose.prod.yml ps
    return 1
}

# 显示状态
show_status() {
    echo ""
    print_header "服务状态"
    docker-compose -f docker-compose.prod.yml ps
}

# 运行健康检查
health_check() {
    print_info "运行健康检查..."

    local endpoints=(
        "http://localhost/health"
        "http://localhost:8080/actuator/health"
        "http://localhost:8001/health"
    )

    for endpoint in "${endpoints[@]}"; do
        if curl -f -s "$endpoint" > /dev/null; then
            print_success "$endpoint - OK"
        else
            print_error "$endpoint - FAILED"
        fi
    done
}

# 显示访问信息
show_access_info() {
    echo ""
    print_header "访问信息"

    echo -e "${GREEN}生产环境:${NC}"
    echo "  前端:        http://localhost"
    echo "  API:         http://localhost/api"
    echo "  Swagger UI:  http://localhost:8080/swagger-ui.html"
    echo "  API Docs:    http://localhost:8001/docs"

    echo ""
    print_warning "注意事项:"
    echo "  1. 生产环境不暴露数据库端口到主机"
    echo "  2. 使用 Nginx 作为反向代理"
    echo "  3. 定期备份数据卷"
    echo "  4. 监控服务健康状态"
    echo ""
}

# 主函数
main() {
    print_header "AIBidComposer 生产环境部署"

    # 检查环境
    if [ ! -f .env ]; then
        print_error ".env 文件不存在"
        exit 1
    fi

    source .env
    check_production_env
    check_docker

    # 询问确认
    print_warning "即将部署到生产环境"
    read -p "确认继续？(y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "已取消部署"
        exit 0
    fi

    # 部署流程
    stop_services
    build_images
    start_services
    wait_for_services

    # 验证
    show_status
    health_check
    show_access_info

    print_success "生产环境部署完成！"
}

# 运行主函数
main
