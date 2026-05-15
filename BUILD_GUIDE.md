# 编译和运行指南

## 快速编译

### 1. 命令行编译
```bash
cd /workspace/finance-tracker
./gradlew clean assembleDebug
```

### 2. 查看编译输出
编译成功后，APK 文件位于：
```
finance-tracker/app/build/outputs/apk/debug/app-debug.apk
```

### 3. 安装到设备
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Android Studio 编译

1. 打开 Android Studio
2. File → Open → 选择 `/workspace/finance-tracker` 目录
3. 等待 Gradle 同步完成
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. 或使用 Run 按钮直接运行到模拟器/真机

## 常见问题解决

### Q1: Gradle 同步失败
```bash
# 清理 Gradle 缓存
./gradlew cleanBuildCache

# 重新同步
./gradlew build --refresh-dependencies
```

### Q2: SDK 未找到
在 Android Studio 中：
- File → Settings → Appearance & Behavior → System Settings → Android SDK
- 安装 Android API 34

### Q3: Kotlin 版本不匹配
检查 `build.gradle.kts` 中的 Kotlin 版本：
```kotlin
id("org.jetbrains.kotlin.android") version "1.9.20"
```

### Q4: Hilt 编译错误
清理并重建：
```bash
./gradlew clean
./gradlew build
```

## 依赖版本说明

- **Android Gradle Plugin**: 8.2.0
- **Kotlin**: 1.9.20
- **Compose BOM**: 2023.10.01
- **Room**: 2.6.1
- **Hilt**: 2.48.1
- **Target SDK**: 34 (Android 14)
- **Min SDK**: 26 (Android 8.0)

## 编译时间优化

首次编译可能需要 5-10 分钟，后续编译会更快。

如需加速编译：
```bash
# 使用 Gradle 守护进程
./gradlew assembleDebug --daemon

# 并行编译
./gradlew assembleDebug --parallel
```

## 调试模式

如果遇到编译错误，查看详细日志：
```bash
./gradlew assembleDebug --stacktrace --info
```
