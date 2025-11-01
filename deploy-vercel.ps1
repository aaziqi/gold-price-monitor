# Vercel 部署脚本 (PowerShell)

Write-Host "🚀 开始部署黄金价格监控平台到 Vercel..." -ForegroundColor Green

# 检查是否安装了 Node.js
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js 版本: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ 请先安装 Node.js (>=18.0.0)" -ForegroundColor Red
    exit 1
}

# 检查是否安装了 npm
try {
    $npmVersion = npm --version
    Write-Host "✅ npm 版本: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ npm 未找到" -ForegroundColor Red
    exit 1
}

# 安装依赖
Write-Host "📦 安装项目依赖..." -ForegroundColor Yellow
npm install
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 根目录依赖安装失败" -ForegroundColor Red
    exit 1
}

Set-Location frontend
npm install
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 前端依赖安装失败" -ForegroundColor Red
    exit 1
}
Set-Location ..

# 本地构建测试
Write-Host "🔨 执行本地构建测试..." -ForegroundColor Yellow
npm run vercel-build
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 构建失败，请检查错误信息" -ForegroundColor Red
    exit 1
}

Write-Host "✅ 本地构建成功！" -ForegroundColor Green

# 检查是否安装了 Vercel CLI
try {
    $vercelVersion = vercel --version
    Write-Host "✅ Vercel CLI 版本: $vercelVersion" -ForegroundColor Green
} catch {
    Write-Host "📥 安装 Vercel CLI..." -ForegroundColor Yellow
    npm install -g vercel
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Vercel CLI 安装失败" -ForegroundColor Red
        exit 1
    }
}

# 部署到 Vercel
Write-Host "🚀 部署到 Vercel..." -ForegroundColor Yellow
Write-Host "请按照提示登录您的 Vercel 账户..." -ForegroundColor Cyan

vercel --prod
if ($LASTEXITCODE -eq 0) {
    Write-Host "🎉 部署成功！" -ForegroundColor Green
    Write-Host "📱 您的网站现在可以访问了！" -ForegroundColor Green
    Write-Host "🔗 请查看上方的部署 URL" -ForegroundColor Cyan
} else {
    Write-Host "❌ 部署失败，请检查错误信息" -ForegroundColor Red
    Write-Host "💡 提示：" -ForegroundColor Yellow
    Write-Host "   1. 确保已登录 Vercel 账户" -ForegroundColor White
    Write-Host "   2. 检查项目配置是否正确" -ForegroundColor White
    Write-Host "   3. 查看 vercel.json 配置" -ForegroundColor White
}

Write-Host "📚 更多信息请查看 VERCEL_DEPLOYMENT.md" -ForegroundColor Cyan