# 部署指南

本文档详细介绍了黄金价格监控平台的各种部署方式，包括本地开发、Docker 部署和生产环境部署。

## 目录

- [环境要求](#环境要求)
- [本地开发部署](#本地开发部署)
- [Docker 部署](#docker-部署)
- [生产环境部署](#生产环境部署)
- [云平台部署](#云平台部署)
- [监控和维护](#监控和维护)
- [故障排除](#故障排除)

## 环境要求

### 基础要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Java | JDK 17+ | 后端运行环境 |
| Node.js | 16.0+ | 前端构建环境 |
| Maven | 3.6+ | 后端构建工具 |
| Docker | 20.10+ | 容器化部署 |
| Docker Compose | 2.0+ | 多容器编排 |

### 系统要求

| 环境 | CPU | 内存 | 存储 | 网络 |
|------|-----|------|------|------|
| 开发环境 | 2核 | 4GB | 10GB | 1Mbps |
| 测试环境 | 2核 | 8GB | 20GB | 10Mbps |
| 生产环境 | 4核 | 16GB | 50GB | 100Mbps |

## 本地开发部署

### 1. 环境准备

#### 安装 Java 17
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# macOS (使用 Homebrew)
brew install openjdk@17

# 验证安装
java -version
```

#### 安装 Node.js
```bash
# 使用 nvm (推荐)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18
nvm use 18

# 或直接下载安装
# https://nodejs.org/

# 验证安装
node --version
npm --version
```

### 2. 项目配置

#### 克隆项目
```bash
git clone https://github.com/your-username/gold-price-monitor.git
cd gold-price-monitor
```

#### 配置环境变量
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑配置文件
vim .env
```

**环境变量说明**
```bash
# 黄金价格API配置
GOLD_API_KEY=your_api_key_here
GOLD_API_URL=https://api.metals.live/v1/spot/gold

# 应用配置
SERVER_PORT=8080
FRONTEND_PORT=3000

# WebSocket配置
WEBSOCKET_ENDPOINT=/ws/gold-price

# 定时任务配置（秒）
PRICE_UPDATE_INTERVAL=30
```

### 3. 启动服务

#### 启动后端服务
```bash
cd backend

# 使用 Maven Wrapper (推荐)
./mvnw clean install
./mvnw spring-boot:run

# 或使用系统 Maven
mvn clean install
mvn spring-boot:run
```

#### 启动前端服务
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 4. 验证部署

访问以下地址验证服务是否正常：

- 🌐 前端应用: http://localhost:3000
- 🔗 后端API: http://localhost:8080/api/gold/health
- 📊 API文档: http://localhost:8080/api/gold/status

## Docker 部署

### 1. 安装 Docker

#### Linux (Ubuntu/Debian)
```bash
# 更新包索引
sudo apt update

# 安装依赖
sudo apt install apt-transport-https ca-certificates curl gnupg lsb-release

# 添加 Docker 官方 GPG 密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 添加 Docker 仓库
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装 Docker
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 将用户添加到 docker 组
sudo usermod -aG docker $USER
```

#### macOS
```bash
# 使用 Homebrew
brew install --cask docker

# 或下载 Docker Desktop
# https://www.docker.com/products/docker-desktop
```

#### Windows
```powershell
# 下载并安装 Docker Desktop
# https://www.docker.com/products/docker-desktop

# 或使用 Chocolatey
choco install docker-desktop
```

### 2. 快速部署

#### 使用构建脚本
```bash
# 给脚本执行权限
chmod +x scripts/build.sh

# 执行完整构建
./scripts/build.sh

# 或分步执行
./scripts/build.sh --clean    # 清理环境
./scripts/build.sh --backend  # 构建后端
./scripts/build.sh --frontend # 构建前端
./scripts/build.sh --docker   # 构建镜像
./scripts/build.sh --start    # 启动服务
```

#### 手动部署
```bash
# 1. 配置环境变量
cp .env.example .env
vim .env

# 2. 构建镜像
docker-compose build

# 3. 启动服务
docker-compose up -d

# 4. 查看状态
docker-compose ps
docker-compose logs -f
```

### 3. 服务配置

#### Docker Compose 配置
```yaml
version: '3.8'

services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - GOLD_API_KEY=${GOLD_API_KEY}
    networks:
      - gold-monitor-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/gold/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - gold-monitor-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:80/health"]
      interval: 30s
      timeout: 10s
      retries: 3

networks:
  gold-monitor-network:
    driver: bridge
```

## 生产环境部署

### 1. 服务器准备

#### 系统配置
```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y curl wget git vim htop

# 配置防火墙
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw enable

# 配置时区
sudo timedatectl set-timezone Asia/Shanghai
```

#### 安全配置
```bash
# 创建应用用户
sudo useradd -m -s /bin/bash goldmonitor
sudo usermod -aG docker goldmonitor

# 配置 SSH 密钥认证
mkdir -p /home/goldmonitor/.ssh
chmod 700 /home/goldmonitor/.ssh
# 复制公钥到 authorized_keys

# 禁用密码登录
sudo vim /etc/ssh/sshd_config
# PasswordAuthentication no
sudo systemctl restart sshd
```

### 2. 部署应用

#### 使用部署脚本
```bash
# 切换到应用用户
sudo su - goldmonitor

# 克隆项目
git clone https://github.com/your-username/gold-price-monitor.git
cd gold-price-monitor

# 配置环境
cp .env.example .env
vim .env

# 执行部署
chmod +x scripts/deploy.sh
./scripts/deploy.sh --deploy
```

#### 手动部署
```bash
# 1. 准备环境
./scripts/deploy.sh --ssl      # 生成SSL证书
./scripts/deploy.sh --env      # 检查环境

# 2. 构建和启动
docker-compose -f docker-compose.prod.yml build
docker-compose -f docker-compose.prod.yml up -d

# 3. 验证部署
./scripts/deploy.sh --health
```

### 3. 反向代理配置

#### Nginx 配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL 配置
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    # 安全头
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;

    # 前端代理
    location / {
        proxy_pass http://localhost:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket 代理
    location /ws/ {
        proxy_pass http://localhost:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_read_timeout 86400;
    }
}
```

### 4. SSL 证书配置

#### Let's Encrypt 证书
```bash
# 安装 Certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加: 0 12 * * * /usr/bin/certbot renew --quiet
```

#### 自签名证书 (开发环境)
```bash
# 生成私钥
openssl genrsa -out server.key 2048

# 生成证书请求
openssl req -new -key server.key -out server.csr

# 生成自签名证书
openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
```

## 云平台部署

### 1. AWS 部署

#### ECS 部署
```bash
# 安装 AWS CLI
pip install awscli

# 配置 AWS 凭证
aws configure

# 创建 ECS 集群
aws ecs create-cluster --cluster-name gold-monitor

# 部署服务
aws ecs create-service \
  --cluster gold-monitor \
  --service-name gold-monitor-service \
  --task-definition gold-monitor:1 \
  --desired-count 2
```

#### EKS 部署
```yaml
# kubernetes/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gold-monitor
spec:
  replicas: 3
  selector:
    matchLabels:
      app: gold-monitor
  template:
    metadata:
      labels:
        app: gold-monitor
    spec:
      containers:
      - name: backend
        image: gold-monitor/backend:latest
        ports:
        - containerPort: 8080
      - name: frontend
        image: gold-monitor/frontend:latest
        ports:
        - containerPort: 80
```

### 2. Google Cloud 部署

#### Cloud Run 部署
```bash
# 构建镜像
gcloud builds submit --tag gcr.io/PROJECT_ID/gold-monitor

# 部署服务
gcloud run deploy gold-monitor \
  --image gcr.io/PROJECT_ID/gold-monitor \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### 3. Azure 部署

#### Container Instances 部署
```bash
# 创建资源组
az group create --name gold-monitor-rg --location eastus

# 部署容器
az container create \
  --resource-group gold-monitor-rg \
  --name gold-monitor \
  --image gold-monitor/app:latest \
  --dns-name-label gold-monitor \
  --ports 80 443
```

## 监控和维护

### 1. 日志管理

#### 查看日志
```bash
# Docker 日志
docker-compose logs -f
docker-compose logs -f backend
docker-compose logs -f frontend

# 系统日志
sudo journalctl -u docker
sudo journalctl -f
```

#### 日志轮转
```bash
# 配置 Docker 日志轮转
cat > /etc/docker/daemon.json << EOF
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
EOF

sudo systemctl restart docker
```

### 2. 性能监控

#### 系统监控
```bash
# 安装监控工具
sudo apt install htop iotop nethogs

# 查看系统资源
htop                    # CPU和内存
iotop                   # 磁盘I/O
nethogs                 # 网络使用
docker stats            # 容器资源使用
```

#### 应用监控
```bash
# 健康检查
curl http://localhost:8080/api/gold/health
curl http://localhost:80/health

# 性能测试
ab -n 1000 -c 10 http://localhost:8080/api/gold/current
```

### 3. 备份策略

#### 数据备份
```bash
# 创建备份脚本
cat > backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/backup/$(date +%Y%m%d)"
mkdir -p $BACKUP_DIR

# 备份配置文件
cp .env $BACKUP_DIR/
cp docker-compose.yml $BACKUP_DIR/

# 备份日志
docker-compose logs > $BACKUP_DIR/application.log

# 压缩备份
tar -czf $BACKUP_DIR.tar.gz $BACKUP_DIR
rm -rf $BACKUP_DIR

# 清理旧备份 (保留7天)
find /backup -name "*.tar.gz" -mtime +7 -delete
EOF

chmod +x backup.sh
```

#### 自动备份
```bash
# 添加到 crontab
crontab -e
# 每天凌晨2点备份
0 2 * * * /path/to/backup.sh
```

## 故障排除

### 1. 常见问题

#### 服务无法启动
```bash
# 检查端口占用
sudo netstat -tlnp | grep :8080
sudo netstat -tlnp | grep :3000

# 检查 Docker 状态
docker-compose ps
docker-compose logs

# 重启服务
docker-compose restart
```

#### 内存不足
```bash
# 检查内存使用
free -h
docker stats

# 清理 Docker 资源
docker system prune -a
docker volume prune
```

#### 网络连接问题
```bash
# 检查网络连接
ping google.com
curl -I http://localhost:8080/api/gold/health

# 检查防火墙
sudo ufw status
sudo iptables -L
```

### 2. 性能优化

#### JVM 优化
```bash
# 设置 JVM 参数
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
```

#### Nginx 优化
```nginx
# nginx.conf
worker_processes auto;
worker_connections 1024;

# 启用 gzip 压缩
gzip on;
gzip_types text/plain application/json application/javascript text/css;

# 设置缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

### 3. 安全加固

#### 系统安全
```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 配置自动安全更新
sudo apt install unattended-upgrades
sudo dpkg-reconfigure -plow unattended-upgrades

# 安装 fail2ban
sudo apt install fail2ban
sudo systemctl enable fail2ban
```

#### 应用安全
```bash
# 限制文件权限
chmod 600 .env
chmod 700 scripts/

# 定期更新依赖
npm audit fix
./mvnw versions:display-dependency-updates
```

### 4. 回滚策略

#### 快速回滚
```bash
# 使用部署脚本回滚
./scripts/deploy.sh --rollback

# 手动回滚
docker-compose down
docker-compose up -d --force-recreate
```

#### 数据恢复
```bash
# 恢复配置
cp /backup/20240101/.env .
cp /backup/20240101/docker-compose.yml .

# 重启服务
docker-compose up -d
```

## 总结

本部署指南涵盖了从本地开发到生产环境的完整部署流程。根据实际需求选择合适的部署方式：

- **本地开发**: 直接运行源码，便于调试
- **Docker 部署**: 容器化部署，环境一致性好
- **生产环境**: 完整的生产级部署，包含监控和安全配置
- **云平台**: 利用云服务的弹性和可靠性

在部署过程中，请注意：

1. **安全性**: 及时更新系统和依赖，配置防火墙和SSL
2. **监控**: 设置完善的日志和监控系统
3. **备份**: 定期备份重要数据和配置
4. **文档**: 维护部署文档和操作手册

如有问题，请参考故障排除章节或联系技术支持。