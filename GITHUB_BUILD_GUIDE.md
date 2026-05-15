# GitHub 打包指南

## 方法一：GitHub Actions 自动打包（推荐）

### 1. 推送到 GitHub

```bash
# 初始化 git 仓库（如果还没有）
cd /workspace/finance-tracker
git init
git add .
git commit -m "Initial commit"

# 创建 GitHub 仓库后推送
git remote add origin https://github.com/your-username/finance-tracker.git
git branch -M main
git push -u origin main
```

### 2. 自动打包

推送到 GitHub 后，GitHub Actions 会自动：
- ✅ 编译 Debug APK
- ✅ 编译 Release APK（未签名）
- ✅ 上传到 Actions 页面
- ✅ 创建 Release（如果是 main 分支）

### 3. 下载 APK

**方式 A：从 Actions 下载**
1. 打开 GitHub 仓库
2. 点击 "Actions" 标签
3. 选择最新的 workflow run
4. 在页面底部的 "Artifacts" 区域下载 APK

**方式 B：从 Releases 下载**
1. 打开 GitHub 仓库
2. 点击 "Releases" 标签
3. 下载最新的 release 中的 APK 文件

---

## 方法二：本地命令行打包（无需 Android Studio）

### 前提条件
需要安装 JDK 17：
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17

# 验证安装
java -version
```

### 打包步骤

```bash
cd /workspace/finance-tracker

# 1. 清理并编译 Debug 版本
./gradlew clean assembleDebug

# 2. 编译 Release 版本（未签名）
./gradlew assembleRelease

# 3. 查看生成的 APK
ls -lh app/build/outputs/apk/debug/app-debug.apk
ls -lh app/build/outputs/apk/release/app-release-unsigned.apk
```

### 安装到手机

```bash
# 连接手机后执行
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 方法三：使用在线编译服务

### 1. GitPod（推荐）

1. 打开 https://gitpod.io/
2. 输入你的 GitHub 仓库 URL
3. GitPod 会启动一个在线开发环境
4. 在终端执行：
```bash
./gradlew assembleDebug
```

### 2. GitHub Codespaces

1. 在 GitHub 仓库页面
2. 点击 "Code" 按钮
3. 选择 "Codespaces" 标签
4. 创建新的 codespace
5. 在终端执行编译命令

---

## 常见问题

### Q1: gradlew 没有执行权限
```bash
chmod +x gradlew
```

### Q2: JDK 版本不对
确保安装 JDK 17：
```bash
java -version
# 应该显示 17.x.x
```

### Q3: 编译内存不足
编辑 `gradle.properties` 添加：
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

### Q4: Android SDK 未安装
GitHub Actions 会自动安装 SDK，无需担心。

---

## 签名 Release APK（可选）

如果要生成已签名的 Release APK，需要：

### 1. 创建 keystore
```bash
keytool -genkey -v -keystore my-release-key.keystore -alias my-alias -keyalg RSA -keysize 2048 -validity 10000
```

### 2. 配置签名

在 `app/build.gradle.kts` 中添加：
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "my-alias"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 3. 设置 GitHub Secrets

在 GitHub 仓库 Settings → Secrets and variables → Actions 中添加：
- `KEYSTORE_PASSWORD`: 密钥库密码
- `KEY_PASSWORD`: 密钥密码
- `KEYSTORE_FILE`: base64 编码的 keystore 文件

---

## 推荐流程

**最简单的方式：**

1. ✅ 推送到 GitHub
2. ✅ 等待 GitHub Actions 自动编译
3. ✅ 从 Actions 或 Releases 下载 APK
4. ✅ 安装到手机测试

**无需 Android Studio，无需本地环境！**

---

## 快速命令参考

```bash
# 推送到 GitHub
git add .
git commit -m "your message"
git push

# 本地编译（如果有 JDK）
./gradlew assembleDebug

# 查看编译输出
ls -lh app/build/outputs/apk/debug/
```
