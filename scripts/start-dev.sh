#!/bin/bash
# ============================================================================
# AIBidComposer - 开发环境启动脚本
# ============================================================================
# 用途：一键启动开发环境的所有服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
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

# 检查环境变量文件
check_env_file() {
    if [ ! -f .env ]; then
        print_warning ".env 文件不存在，正在从 .env.example 创建..."
        cp .env.example .env
        print_warning "请编辑 .env 文件，填写必要的配置（API Keys 等）"
        print_warning "特别注意：需要配置 OPENAI_API_KEY 或 ANTHROPIC_API_KEY"
        read -p "按 Enter 键继续..."
    fi
}

# 检查 Docker 是否运行
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker 未运行，请先启动 Docker Desktop"
        exit 1
    fi
    print_success "Docker 正在运行"
}

# 检查 Docker Compose 版本
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        print_error "docker-compose 未安装"
        exit 1
    fi

    local version=$(docker-compose version --short)
    print_info "Docker Compose 版本: $version"
}

# 停止并删除所有容器
cleanup() {
    print_info "停止并删除现有容器..."
    docker-compose down -v
}

# 拉取最新镜像
pull_images() {
    print_info "拉取 Docker 镜像..."
    docker-compose pull
}

# 构建应用镜像
build_services() {
    print_info "构建应用服务镜像..."
    docker-compose build --parallel
}

# 启动服务
start_services() {
    print_info "启动所有服务..."
    docker-compose up -d
}

# 等待服务健康
wait_for_services() {
    print_info "等待服务启动完成..."

    local max_attempts=60
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        if docker-compose ps | grep -q "(healthy)"; then
            print_success "服务启动成功！"
            return 0
        fi

        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done

    print_error "服务启动超时"
    docker-compose ps
    return 1
}

# 显示服务状态
show_status() {
    echo ""
    print_header "服务状态"
    docker-compose ps
}

# 显示访问信息
show_access_info() {
    echo ""
    print_header "访问信息"

    echo -e "${GREEN}前端应用:${NC}"
    echo "  http://localhost:5173"

    echo ""
    echo -e "${GREEN}后端服务:${NC}"
    echo "  Java API:     http://localhost:8080"
    echo "  Java Docs:    http://localhost:8080/swagger-ui.html"
    echo "  Python AI:    http://localhost:8001"
    echo "  Python Docs:  http://localhost:8001/docs"

    echo ""
    echo -e "${GREEN}数据服务:${NC}"
    echo "  PostgreSQL:    localhost:5432"
    echo "  Elasticsearch: http://localhost:9200"
    echo "  Redis:         localhost:6379"
    echo "  RabbitMQ Web:  http://localhost:15672 (guest/guest)"
    echo "  MinIO Console: http://localhost:9001 (minioadmin/minioadmin)"

    echo ""
    echo -e "${YELLOW}提示:${NC}"
    echo "  - 查看日志: ${BLUE}docker-compose logs -f [service_name]${NC}"
    echo "  - 停止服务: ${BLUE}docker-compose stop${NC}"
    echo "  - 删除服务: ${BLUE}docker-compose down${NC}"
    echo "  - 删除数据: ${BLUE}docker-compose down -v${NC}"
    echo ""
}

# 显示日志
show_logs() {
    print_info "显示服务日志（按 Ctrl+C 退出）..."
    docker-compose logs -f
}

# 主函数
main() {
    print_header "AIBidComposer 开发环境启动"

    # 检查环境
    check_env_file
    check_docker
    check_docker_compose

    # 询问是否清理
    read -p "是否清理现有容器？(y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        cleanup
    fi

    # 启动流程
    pull_images
    build_services
    start_services
    wait_for_services

    # 显示信息
    show_status
    show_access_info

    # 询问是否查看日志
    read -p "是否查看实时日志？(y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        show_logs
    fi

    print_success "开发环境启动完成！"
}

# 运行主函数
main
