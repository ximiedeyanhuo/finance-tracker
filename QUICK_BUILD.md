# 🚀 快速打包指南 - 无需 Android Studio

## 已完成 ✅

- ✅ Git 仓库已初始化
- ✅ 所有代码已提交
- ✅ GitHub Actions 配置文件已创建
- ✅ 项目位于：`/workspace/finance-tracker`

---

## 方法一：GitHub Actions 自动打包（最简单）

### 步骤

#### 1. 创建 GitHub 仓库
```
1. 打开 https://github.com/new
2. 仓库名：finance-tracker
3. 设为公开（Public）
4. 点击 "Create repository"
```

#### 2. 推送代码到 GitHub
```bash
cd /workspace/finance-tracker

# 替换为你的 GitHub 用户名
git remote add origin https://github.com/YOUR_USERNAME/finance-tracker.git

# 推送
git push -u origin master
```

#### 3. 等待自动编译
- 推送到 GitHub 后，Actions 会自动开始编译
- 大约需要 5-10 分钟
- 编译完成后会自动创建 Release

#### 4. 下载 APK
**方式 A：从 Actions 下载**
```
1. 打开 GitHub 仓库
2. 点击 "Actions" 标签
3. 选择最新的 workflow
4. 滚动到页面底部
5. 点击 "app-debug" 下载 APK
```

**方式 B：从 Releases 下载**
```
1. 打开 GitHub 仓库
2. 点击 "Releases" 标签
3. 下载最新的 APK 文件
```

#### 5. 安装到手机
```bash
# 通过 USB 连接手机后
adb install app-debug.apk
```

或直接通过微信/QQ 发送到手机安装

---

## 方法二：本地命令行打包

### 前提条件
只需安装 JDK 17：

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**macOS:**
```bash
brew install openjdk@17
```

### 打包命令
```bash
cd /workspace/finance-tracker

# 编译 Debug 版本
./gradlew assembleDebug

# 编译 Release 版本（未签名）
./gradlew assembleRelease
```

### APK 位置
```
Debug 版本：
app/build/outputs/apk/debug/app-debug.apk

Release 版本：
app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 方法三：使用 GitPod（在线 IDE）

### 步骤

1. 推送到 GitHub（参考方法一）

2. 打开 GitPod
```
https://gitpod.io/#https://github.com/YOUR_USERNAME/finance-tracker
```

3. GitPod 会启动在线开发环境

4. 在终端执行：
```bash
./gradlew assembleDebug
```

5. 下载生成的 APK：
```bash
# GitPod 会把文件放在 workspace 中
ls app/build/outputs/apk/debug/
```

---

## 手机安装说明

### 方式 1：通过 USB 安装
```bash
# 1. 手机开启 USB 调试
# 2. 连接电脑
# 3. 执行
adb install app-debug.apk
```

### 方式 2：通过文件传输
```
1. 把 APK 文件传到手机
2. 在手机上打开 APK 文件
3. 允许"安装未知来源"
4. 点击安装
```

### 方式 3：通过二维码
```
1. 使用在线工具生成 APK 下载链接的二维码
2. 手机扫码下载并安装
```

---

## 常见问题

### Q1: 提示 gradlew 没有执行权限
```bash
chmod +x gradlew
```

### Q2: 推送时提示权限问题
```bash
# 使用 HTTPS 方式
git remote add origin https://github.com/YOUR_USERNAME/finance-tracker.git

# 或使用 SSH 方式
git remote add origin git@github.com:YOUR_USERNAME/finance-tracker.git
```

### Q3: GitHub Actions 编译失败
- 检查 `.github/workflows/android-ci.yml` 是否正确
- 查看 Actions 日志详情
- 确保 `build.gradle.kts` 配置正确

### Q4: APK 安装失败
- 手机设置 → 安全 → 允许安装未知来源应用
- 确保 APK 文件完整下载
- 尝试不同安装方式

---

## 推荐流程

**最简单的方式（5 分钟搞定）：**

```bash
# 1. 推送到 GitHub
cd /workspace/finance-tracker
git remote add origin https://github.com/YOUR_USERNAME/finance-tracker.git
git push -u origin master

# 2. 打开 GitHub 仓库页面

# 3. 点击 Actions 标签，等待编译完成（5-10 分钟）

# 4. 下载 APK 并安装到手机
```

**无需 Android Studio，无需本地配置环境！**

---

## GitHub Actions 会自动

- ✅ 安装 JDK 17
- ✅ 安装 Android SDK
- ✅ 下载 Gradle 依赖
- ✅ 编译 Debug APK
- ✅ 编译 Release APK
- ✅ 上传到 Actions 页面
- ✅ 创建 GitHub Release

**全自动，零配置！**

---

## 下一步

1. **推送到 GitHub** - 参考上面的步骤
2. **等待自动编译** - Actions 会自动打包
3. **下载 APK** - 从 Actions 或 Releases 下载
4. **安装测试** - 安装到手机测试功能

---

**项目已完全准备就绪，可以立即推送打包！** 🎉
