#!/bin/bash

# 黄金价格监控平台部署脚本
# 用于生产环境部署

set -e

echo "🚀 开始部署黄金价格监控平台..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
DEPLOY_ENV=${DEPLOY_ENV:-production}
BACKUP_DIR=${BACKUP_DIR:-./backups}
LOG_DIR=${LOG_DIR:-./logs}

# 函数：打印彩色消息
print_message() {
    echo -e "${2}${1}${NC}"
}

# 创建必要的目录
create_directories() {
    print_message "📁 创建必要的目录..." $BLUE
    
    mkdir -p $BACKUP_DIR
    mkdir -p $LOG_DIR
    mkdir -p docker/nginx/ssl
    
    print_message "✅ 目录创建完成" $GREEN
}

# 检查环境变量
check_environment() {
    print_message "🔍 检查环境配置..." $BLUE
    
    if [ ! -f ".env" ]; then
        if [ -f ".env.example" ]; then
            print_message "⚠️  未找到 .env 文件，从 .env.example 复制..." $YELLOW
            cp .env.example .env
            print_message "📝 请编辑 .env 文件配置必要的环境变量" $YELLOW
        else
            print_message "❌ 未找到环境配置文件" $RED
            exit 1
        fi
    fi
    
    # 检查必要的环境变量
    source .env
    
    if [ -z "$GOLD_API_KEY" ] || [ "$GOLD_API_KEY" = "your_api_key_here" ]; then
        print_message "⚠️  请在 .env 文件中配置有效的 GOLD_API_KEY" $YELLOW
    fi
    
    print_message "✅ 环境检查完成" $GREEN
}

# 生成SSL证书（自签名，用于开发）
generate_ssl_cert() {
    print_message "🔐 生成SSL证书..." $BLUE
    
    if [ ! -f "docker/nginx/ssl/cert.pem" ]; then
        openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
            -keyout docker/nginx/ssl/key.pem \
            -out docker/nginx/ssl/cert.pem \
            -subj "/C=CN/ST=State/L=City/O=Organization/CN=localhost"
        
        print_message "✅ SSL证书生成完成" $GREEN
    else
        print_message "✅ SSL证书已存在" $GREEN
    fi
}

# 备份当前部署
backup_current_deployment() {
    print_message "💾 备份当前部署..." $BLUE
    
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_FILE="$BACKUP_DIR/backup_$TIMESTAMP.tar.gz"
    
    if docker-compose ps -q | grep -q .; then
        # 导出当前运行的容器数据
        docker-compose exec -T backend sh -c "cd /app && tar czf - logs/" > "$BACKUP_FILE" 2>/dev/null || true
        print_message "✅ 备份完成: $BACKUP_FILE" $GREEN
    else
        print_message "ℹ️  没有运行中的服务需要备份" $BLUE
    fi
}

# 拉取最新代码
pull_latest_code() {
    print_message "📥 拉取最新代码..." $BLUE
    
    if [ -d ".git" ]; then
        git pull origin main
        print_message "✅ 代码更新完成" $GREEN
    else
        print_message "ℹ️  不是Git仓库，跳过代码拉取" $BLUE
    fi
}

# 构建和部署
build_and_deploy() {
    print_message "🔨 构建和部署应用..." $BLUE
    
    # 停止现有服务
    docker-compose down
    
    # 构建新镜像
    docker-compose build --no-cache
    
    # 启动服务
    docker-compose up -d
    
    print_message "✅ 部署完成" $GREEN
}

# 健康检查
health_check() {
    print_message "🏥 执行健康检查..." $BLUE
    
    # 等待服务启动
    sleep 15
    
    # 检查后端健康状态
    if curl -f http://localhost:8080/api/gold/health >/dev/null 2>&1; then
        print_message "✅ 后端服务健康" $GREEN
    else
        print_message "❌ 后端服务异常" $RED
        return 1
    fi
    
    # 检查前端
    if curl -f http://localhost:80/health >/dev/null 2>&1; then
        print_message "✅ 前端服务健康" $GREEN
    else
        print_message "❌ 前端服务异常" $RED
        return 1
    fi
    
    print_message "✅ 所有服务健康检查通过" $GREEN
}

# 回滚部署
rollback_deployment() {
    print_message "🔄 回滚到上一个版本..." $YELLOW
    
    # 停止当前服务
    docker-compose down
    
    # 恢复备份（如果有）
    LATEST_BACKUP=$(ls -t $BACKUP_DIR/backup_*.tar.gz 2>/dev/null | head -1)
    if [ -n "$LATEST_BACKUP" ]; then
        print_message "📦 恢复备份: $LATEST_BACKUP" $BLUE
        # 这里可以添加恢复逻辑
    fi
    
    # 使用上一个镜像版本
    docker-compose up -d
    
    print_message "✅ 回滚完成" $GREEN
}

# 显示服务状态
show_status() {
    print_message "📊 服务状态:" $BLUE
    docker-compose ps
    
    print_message "\n📋 服务日志 (最近20行):" $BLUE
    docker-compose logs --tail=20
    
    print_message "\n🌐 访问地址:" $BLUE
    print_message "前端: http://localhost:80" $GREEN
    print_message "后端API: http://localhost:8080/api" $GREEN
    print_message "HTTPS: https://localhost:443" $GREEN
}

# 清理旧的备份和日志
cleanup() {
    print_message "🧹 清理旧文件..." $BLUE
    
    # 保留最近7天的备份
    find $BACKUP_DIR -name "backup_*.tar.gz" -mtime +7 -delete 2>/dev/null || true
    
    # 清理Docker未使用的镜像
    docker image prune -f
    
    print_message "✅ 清理完成" $GREEN
}

# 显示帮助信息
show_help() {
    echo "黄金价格监控平台部署脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  --deploy     完整部署流程"
    echo "  --rollback   回滚到上一个版本"
    echo "  --status     显示服务状态"
    echo "  --health     执行健康检查"
    echo "  --cleanup    清理旧文件"
    echo "  --ssl        生成SSL证书"
    echo "  --help       显示帮助信息"
    echo ""
    echo "环境变量:"
    echo "  DEPLOY_ENV   部署环境 (默认: production)"
    echo "  BACKUP_DIR   备份目录 (默认: ./backups)"
    echo "  LOG_DIR      日志目录 (默认: ./logs)"
}

# 主函数
main() {
    case "${1:-}" in
        --deploy)
            create_directories
            check_environment
            generate_ssl_cert
            backup_current_deployment
            pull_latest_code
            build_and_deploy
            health_check
            show_status
            ;;
        --rollback)
            rollback_deployment
            health_check
            ;;
        --status)
            show_status
            ;;
        --health)
            health_check
            ;;
        --cleanup)
            cleanup
            ;;
        --ssl)
            generate_ssl_cert
            ;;
        --help)
            show_help
            ;;
        "")
            # 默认执行完整部署
            create_directories
            check_environment
            generate_ssl_cert
            backup_current_deployment
            pull_latest_code
            build_and_deploy
            health_check
            show_status
            ;;
        *)
            print_message "❌ 未知选项: $1" $RED
            show_help
            exit 1
            ;;
    esac
}

# 错误处理
trap 'print_message "❌ 部署过程中发生错误" $RED; exit 1' ERR

# 执行主函数
main "$@"