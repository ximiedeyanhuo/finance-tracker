# 安卓记账本 - 运行说明

## 项目简介

安卓记账本是一款面向个人用户的本地记账工具，采用 Kotlin + Jetpack Compose + Room + Hilt 构建。所有数据纯本地存储，确保隐私安全可控。

## 技术栈

- **语言**: Kotlin 1.9.20
- **UI 框架**: Jetpack Compose
- **架构模式**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room (SQLite)
- **图片加载**: Coil
- **CSV 导出**: OpenCSV

## 系统要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK API 34
- 最低支持 Android 8.0 (API 26)

## 编译步骤

### 1. 打开项目

在 Android Studio 中打开项目根目录 `/workspace/finance-tracker`

### 2. 同步 Gradle

Android Studio 会自动同步 Gradle 依赖。如果没有自动同步，点击 "Sync Now" 按钮。

### 3. 构建项目

**命令行方式:**
```bash
cd /workspace/finance-tracker

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

**Android Studio 方式:**
- 菜单栏选择 Build → Build Bundle(s) / APK(s) → Build APK(s)

### 4. 生成的 APK 位置

构建成功后，APK 文件位于：

```
# Debug 版本
finance-tracker/app/build/outputs/apk/debug/app-debug.apk

# Release 版本
finance-tracker/app/build/outputs/apk/release/app-release-unsigned.apk
```

## 安装到手机

### 方式一：通过 Android Studio 安装

1. 用 USB 线连接安卓手机和电脑
2. 在手机上开启"开发者选项"和"USB 调试"
3. 在 Android Studio 中点击 Run 按钮 (绿色三角形)
4. 选择连接的手机作为目标设备

### 方式二：手动安装 APK

1. 将生成的 APK 文件复制到手机
2. 在手机上打开 APK 文件
3. 允许"安装未知来源应用"权限
4. 点击"安装"

### 方式三：通过 ADB 安装

```bash
# 连接手机后执行
adb install finance-tracker/app/build/outputs/apk/debug/app-debug.apk
```

## 功能清单

### 已实现功能

- [x] 财务明细列表（新增、修改、删除、查看详情）
- [x] 财务记录筛选（收支类型、图片筛选、备注关键词、日期范围）
- [x] 财务记录导出（CSV 格式）
- [x] 日历视图（月度日历、每日收支、当日明细）
- [x] 日账统计（按日汇总、导出）
- [x] 周账统计（按周汇总、导出明细）
- [x] 月账统计（按月汇总、导出明细）
- [x] 年账统计（按年汇总、导出明细）
- [x] 收支类型管理（新增、修改、删除保护）
- [x] 分类统计详情
- [x] 图片上传（拍照/相册）
- [x] 预设收支类型初始化

### 待完善功能

- [ ] 底部导航栏完整集成
- [ ] 所有 UI 页面的完整实现
- [ ] 图片压缩和缓存优化
- [ ] 深色模式支持
- [ ] 应用锁功能（可选）

## 项目结构

```
finance-tracker/
├── app/
│   ├── src/main/
│   │   ├── java/com/monkeycode/financetracker/
│   │   │   ├── data/              # 数据层
│   │   │   │   ├── local/         # Room DAO 和 Database
│   │   │   │   ├── mapper/        # 数据映射
│   │   │   │   ├── model/         # 数据实体
│   │   │   │   └── repository/    # 数据仓库
│   │   │   ├── di/                # 依赖注入模块
│   │   │   ├── domain/            # 领域层
│   │   │   │   ├── model/         # 领域模型
│   │   │   │   └── service/       # 领域服务
│   │   │   ├── ui/                # UI 层
│   │   │   │   ├── navigation/    # 导航图
│   │   │   │   ├── screens/       # 页面组件
│   │   │   │   ├── theme/         # 主题系统
│   │   │   │   └── viewmodel/     # ViewModel
│   │   │   ├── FinanceTrackerApp.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── values/            # 字符串、颜色、主题
│   │   │   └── xml/               # 配置文件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── build.gradle.kts
```

## 数据说明

### 预设收支类型

**收入类型:**
- 工资
- 转账
- 理财赎回
- 退税

**支出类型:**
- 日常消费
- 房贷
- 借呗
- 花呗
- 京东白条
- 信用卡 - 平安
- 信用卡 - 邮储
- 投资理财
- 人情往来

### 数据库

数据库名称：`finance_tracker_db`

数据库文件位置：`/data/data/com.monkeycode.financetracker/databases/finance_tracker_db`

### 图片存储

图片存储在应用私有目录：`/data/data/com.monkeycode.financetracker/files/images/`

## 常见问题

### Q: 构建时出现"SDK 未安装"错误
A: 打开 Android Studio 的 SDK Manager，安装 Android API 34

### Q: 模拟器上无法使用相机功能
A: 模拟器相机功能受限，建议使用"从相册选择"，或在真机上测试

### Q: CSV 导出后无法打开
A: 使用系统分享功能将 CSV 文件分享到微信、邮件或文件管理器

### Q: 如何备份数据？
A: 当前版本使用本地存储，可以通过以下路径手动备份数据库：
```
/data/data/com.monkeycode.financetracker/databases/finance_tracker_db
```
需要 root 权限或使用 adb backup 命令

## 后续计划

1. **完善所有 UI 页面** - 完成日历、统计页面的完整实现
2. **优化性能** - 万级数据量下的查询优化
3. **增强导出功能** - 支持 Excel 格式导出
4. **数据同步** - 可选的 WebDAV 云端备份（二期）
5. **应用锁** - PIN 码/图案锁/生物识别（二期）
6. **深色模式** - 完整的深色主题支持

## 开发团队

MonkeyCode-AI 智能开发平台

## 许可证

本项目仅供学习和个人使用。
