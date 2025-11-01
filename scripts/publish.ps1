Param(
  [Parameter(Mandatory=$true)][string]$User,
  [Parameter(Mandatory=$true)][string]$Token,
  [Parameter(Mandatory=$true)][string]$Repo,
  [string]$Branch = "main",
  [switch]$CreateRepo,
  [switch]$CreateTag,
  [string]$Tag = "v1.0.0",
  [string]$TagMessage = "Initial release",
  [switch]$CreateRelease
)

$ErrorActionPreference = "Stop"

function Write-Info($msg) { Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Ok($msg)   { Write-Host "[OK]   $msg" -ForegroundColor Green }
function Write-Err($msg)  { Write-Host "[ERR]  $msg" -ForegroundColor Red }

# 1) 基本检查
try {
  git --version | Out-Null
} catch {
  Write-Err "未检测到 Git，请先安装 Git。"
  exit 1
}

if (!(Test-Path ".git")) {
  Write-Info "当前目录未初始化为 Git 仓库，正在初始化..."
  git init | Out-Null
}

# 2) 可选：在GitHub上创建仓库
if ($CreateRepo) {
  Write-Info "尝试在 GitHub 创建仓库 '$Repo' (用户: $User) ..."
  $createRepoBody = @{ name = $Repo; private = $false } | ConvertTo-Json
  try {
    $repoResp = Invoke-RestMethod -Uri "https://api.github.com/user/repos" -Method POST -Headers @{ Authorization = "token $Token"; 'User-Agent' = "Trae-Publish" } -ContentType "application/json" -Body $createRepoBody
    Write-Ok "仓库创建成功：https://github.com/$User/$Repo"
  } catch {
    Write-Info "仓库可能已存在，继续后续步骤。详情："; Write-Host $_
  }
}

# 3) 设置主分支
try {
  Write-Info "设置主分支为 '$Branch'"
  git branch -M $Branch
} catch {
  Write-Info "设置主分支时遇到问题，继续..."
}

# 4) 配置远程 origin（包含令牌）
$remoteUrl = "https://$User:$Token@github.com/$User/$Repo.git"
try {
  $existing = git remote get-url origin 2>$null
  if ($LASTEXITCODE -eq 0 -and $existing) {
    Write-Info "更新 origin 远程地址"
    git remote set-url origin $remoteUrl | Out-Null
  } else {
    Write-Info "添加 origin 远程地址"
    git remote add origin $remoteUrl | Out-Null
  }
} catch {
  Write-Err "配置远程仓库失败：$_"; exit 1
}

# 5) 可选：创建版本标签
if ($CreateTag) {
  $hasTag = git tag -l $Tag
  if (-not $hasTag) {
    Write-Info "创建标签 $Tag"
    git tag -a $Tag -m $TagMessage
  } else {
    Write-Info "标签 $Tag 已存在"
  }
}

# 6) 推送代码与标签
Write-Info "推送分支 '$Branch' 到 GitHub"
try {
  git push -u origin $Branch
  Write-Ok "分支推送完成"
} catch {
  Write-Err "推送分支失败：$_"; exit 1
}

Write-Info "推送标签到 GitHub"
try {
  git push origin --tags
  Write-Ok "标签推送完成"
} catch {
  Write-Err "推送标签失败：$_"; exit 1
}

# 7) 可选：创建 GitHub Release
if ($CreateRelease) {
  Write-Info "创建 GitHub Release: $Tag"
  $releaseBody = @{ tag_name = $Tag; name = $Tag; body = "Initial release of Gold Price Monitor."; draft = $false; prerelease = $false } | ConvertTo-Json
  try {
    $relResp = Invoke-RestMethod -Uri "https://api.github.com/repos/$User/$Repo/releases" -Method POST -Headers @{ Authorization = "token $Token"; 'User-Agent' = "Trae-Publish" } -ContentType "application/json" -Body $releaseBody
    Write-Ok "Release 创建成功：$($relResp.html_url)"
  } catch {
    Write-Info "创建 Release 失败或已存在，详情："; Write-Host $_
  }
}

Write-Ok "全部完成。您可以现在访问：https://github.com/$User/$Repo"