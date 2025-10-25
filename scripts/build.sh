#!/bin/bash

# 黄金价格监控平台构建脚本
# 用于构建和部署整个应用

set -e

echo "🚀 开始构建黄金价格监控平台..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 函数：打印彩色消息
print_message() {
    echo -e "${2}${1}${NC}"
}

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_message "❌ Docker 未安装，请先安装 Docker" $RED
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_message "❌ Docker Compose 未安装，请先安装 Docker Compose" $RED
        exit 1
    fi
    
    print_message "✅ Docker 环境检查通过" $GREEN
}

# 清理旧的构建
clean_build() {
    print_message "🧹 清理旧的构建文件..." $YELLOW
    
    # 停止并删除容器
    docker-compose down --remove-orphans 2>/dev/null || true
    
    # 删除旧的镜像
    docker rmi gold-price-monitor-backend:latest 2>/dev/null || true
    docker rmi gold-price-monitor-frontend:latest 2>/dev/null || true
    
    print_message "✅ 清理完成" $GREEN
}

# 构建后端
build_backend() {
    print_message "🔨 构建后端服务..." $BLUE
    
    cd backend
    
    # 检查Java环境
    if ! command -v java &> /dev/null; then
        print_message "❌ Java 未安装，请先安装 Java 17+" $RED
        exit 1
    fi
    
    # Maven构建
    if [ -f "mvnw" ]; then
        chmod +x mvnw
        ./mvnw clean package -DskipTests
    else
        mvn clean package -DskipTests
    fi
    
    cd ..
    print_message "✅ 后端构建完成" $GREEN
}

# 构建前端
build_frontend() {
    print_message "🎨 构建前端应用..." $BLUE
    
    cd frontend
    
    # 检查Node.js环境
    if ! command -v node &> /dev/null; then
        print_message "❌ Node.js 未安装，请先安装 Node.js 16+" $RED
        exit 1
    fi
    
    # 安装依赖
    npm ci
    
    # 构建应用
    npm run build
    
    cd ..
    print_message "✅ 前端构建完成" $GREEN
}

# Docker构建
build_docker() {
    print_message "🐳 构建Docker镜像..." $BLUE
    
    # 构建镜像
    docker-compose build --no-cache
    
    print_message "✅ Docker镜像构建完成" $GREEN
}

# 启动服务
start_services() {
    print_message "🚀 启动服务..." $BLUE
    
    # 启动服务
    docker-compose up -d
    
    # 等待服务启动
    print_message "⏳ 等待服务启动..." $YELLOW
    sleep 10
    
    # 检查服务状态
    if docker-compose ps | grep -q "Up"; then
        print_message "✅ 服务启动成功" $GREEN
        print_message "🌐 前端地址: http://localhost:3000" $BLUE
        print_message "🔗 后端API: http://localhost:8080/api" $BLUE
        print_message "📊 WebSocket: ws://localhost:8080/ws/gold-price" $BLUE
    else
        print_message "❌ 服务启动失败，请检查日志" $RED
        docker-compose logs
        exit 1
    fi
}

# 显示帮助信息
show_help() {
    echo "黄金价格监控平台构建脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  --clean     清理旧的构建文件"
    echo "  --backend   只构建后端"
    echo "  --frontend  只构建前端"
    echo "  --docker    只构建Docker镜像"
    echo "  --start     启动服务"
    echo "  --help      显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0                # 完整构建和启动"
    echo "  $0 --clean        # 清理构建文件"
    echo "  $0 --backend      # 只构建后端"
}

# 主函数
main() {
    case "${1:-}" in
        --clean)
            check_docker
            clean_build
            ;;
        --backend)
            build_backend
            ;;
        --frontend)
            build_frontend
            ;;
        --docker)
            check_docker
            build_docker
            ;;
        --start)
            check_docker
            start_services
            ;;
        --help)
            show_help
            ;;
        "")
            # 完整构建流程
            check_docker
            clean_build
            build_backend
            build_frontend
            build_docker
            start_services
            ;;
        *)
            print_message "❌ 未知选项: $1" $RED
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"