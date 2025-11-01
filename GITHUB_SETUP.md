# GitHub 仓库设置指南

## 步骤1: 在GitHub上创建新仓库

1. 访问 [GitHub](https://github.com) 并登录您的账户
2. 点击右上角的 "+" 按钮，选择 "New repository"
3. 填写仓库信息：
   - **Repository name**: `gold-price-monitor`
   - **Description**: `🏆 实时黄金价格监控与可视化平台 - Real-time Gold Price Monitoring Platform`
   - **Visibility**: 选择 Public 或 Private
   - **不要**勾选 "Initialize this repository with a README"（因为我们已经有了）
4. 点击 "Create repository"

## 步骤2: 连接本地仓库到GitHub

在项目根目录执行以下命令（将 `your-username` 替换为您的GitHub用户名）：

```bash
# 添加远程仓库
git remote add origin https://github.com/your-username/gold-price-monitor.git

# 设置主分支名称
git branch -M main

# 推送代码到GitHub
git push -u origin main
```

## 步骤3: 验证上传

1. 刷新GitHub仓库页面
2. 确认所有文件都已上传
3. 检查README.md是否正确显示

## 可选：设置GitHub Pages（如果需要在线演示）

1. 在仓库页面，点击 "Settings"
2. 滚动到 "Pages" 部分
3. 在 "Source" 下选择 "Deploy from a branch"
4. 选择 "main" 分支和 "/ (root)" 文件夹
5. 点击 "Save"

## 可选：添加仓库标签和主题

1. 在仓库主页，点击设置图标（齿轮）
2. 添加标签：`spring-boot`, `vue3`, `websocket`, `gold-price`, `real-time`, `docker`
3. 添加网站链接（如果有部署的话）

## 后续维护

- 定期推送更新：`git push origin main`
- 创建发布版本：在GitHub上创建 Release
- 设置 Issues 和 Discussions 来管理用户反馈