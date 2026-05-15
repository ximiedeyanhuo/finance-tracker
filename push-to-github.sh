#!/bin/bash

# 自动推送到 GitHub 的脚本
# 使用方法：./push-to-github.sh

echo "🚀 开始推送到 GitHub..."
echo ""

# 检查是否在正确的目录
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ 错误：请在 finance-tracker 目录下运行此脚本"
    exit 1
fi

# 添加远程仓库
echo "📡 配置远程仓库..."
git remote set-url origin https://github.com/ximiedeyanhuo/finance-tracker.git 2>/dev/null || \
git remote add origin https://github.com/ximiedeyanhuo/finance-tracker.git

# 重命名分支为 main
echo "🔄 重命名分支为 main..."
git branch -M main 2>/dev/null

# 推送到 GitHub
echo "📤 推送到 GitHub..."
echo ""
echo "💡 提示：首次推送需要输入 GitHub 用户名和密码（或 Personal Access Token）"
echo ""

git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 推送成功！"
    echo ""
    echo "📱 下一步："
    echo "1. 打开 https://github.com/ximiedeyanhuo/finance-tracker"
    echo "2. 点击 'Actions' 标签查看编译进度"
    echo "3. 等待 5-10 分钟编译完成"
    echo "4. 从 Actions 或 Releases 下载 APK"
    echo ""
    echo "📦 编译完成后下载地址："
    echo "   - Actions: https://github.com/ximiedeyanhuo/finance-tracker/actions"
    echo "   - Releases: https://github.com/ximiedeyanhuo/finance-tracker/releases"
    echo ""
else
    echo ""
    echo "❌ 推送失败"
    echo ""
    echo "💡 可能的原因和解决方法："
    echo ""
    echo "1. 需要 GitHub 认证："
    echo "   - 使用 Personal Access Token 代替密码"
    echo "   - 创建 Token: https://github.com/settings/tokens"
    echo "   - 权限：repo, workflow"
    echo ""
    echo "2. 使用 SSH 方式推送："
    echo "   git remote set-url origin git@github.com:ximiedeyanhuo/finance-tracker.git"
    echo "   git push -u origin main"
    echo ""
    echo "3. 仓库不存在："
    echo "   - 先在 GitHub 创建仓库：https://github.com/new"
    echo "   - 仓库名：finance-tracker"
    echo ""
fi
