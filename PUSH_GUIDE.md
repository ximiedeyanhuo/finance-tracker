# 📤 推送到 GitHub 指南

## ⚠️ 重要说明

由于 GitHub 需要身份认证，需要您手动执行推送操作。

---

## 方法一：使用推送脚本（推荐）

```bash
cd /workspace/finance-tracker
./push-to-github.sh
```

脚本会自动：
- ✅ 配置远程仓库
- ✅ 重命名分支为 main
- ✅ 推送到 GitHub
- ✅ 提供下一步指引

---

## 方法二：手动推送

### 1. 配置远程仓库
```bash
cd /workspace/finance-tracker
git remote add origin https://github.com/ximiedeyanhuo/finance-tracker.git
# 或如果已存在
git remote set-url origin https://github.com/ximiedeyanhuo/finance-tracker.git
```

### 2. 重命名分支
```bash
git branch -M main
```

### 3. 推送到 GitHub
```bash
git push -u origin main
```

**首次推送需要认证：**
- 用户名：`ximiedeyanhuo`
- 密码：**Personal Access Token**（不是 GitHub 密码）

---

## 🔑 获取 Personal Access Token

### 步骤

1. 打开 https://github.com/settings/tokens

2. 点击 "Generate new token (classic)"

3. 填写信息：
   - **Note**: `finance-tracker-deploy`
   - **Expiration**: `90 days` 或更长
   - **Select scopes**: 勾选以下权限
     - ✅ `repo` (Full control of private repositories)
     - ✅ `workflow` (Update GitHub Action workflows)

4. 点击 "Generate token"

5. **复制生成的 Token**（只显示一次，务必保存！）

6. 使用 Token 推送：
```bash
# 方式 A：交互式推送
git push -u origin main
# 输入用户名：ximiedeyanhuo
# 输入密码：粘贴刚才复制的 Token

# 方式 B：在 URL 中包含 Token（不推荐，会暴露 Token）
git remote set-url origin https://ximiedeyanhuo:YOUR_TOKEN_HERE@github.com/ximiedeyanhuo/finance-tracker.git
git push -u origin main
```

---

## 方法三：使用 SSH 推送（免密码）

### 1. 生成 SSH 密钥
```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
# 一路回车
```

### 2. 添加公钥到 GitHub
```bash
cat ~/.ssh/id_ed25519.pub
# 复制输出内容
```

然后：
1. 打开 https://github.com/settings/keys
2. 点击 "New SSH key"
3. 粘贴公钥内容
4. 点击 "Add SSH key"

### 3. 切换到 SSH 方式
```bash
cd /workspace/finance-tracker
git remote set-url origin git@github.com:ximiedeyanhuo/finance-tracker.git
git push -u origin main
```

---

## 推送成功后的操作

### 1. 查看编译进度
打开：https://github.com/ximiedeyanhuo/finance-tracker/actions

你会看到 "Android CI/CD" workflow 正在运行

### 2. 等待编译完成
大约需要 5-10 分钟

### 3. 下载 APK

**方式 A：从 Actions 下载**
1. 点击最新的 workflow run
2. 滚动到页面底部
3. 在 "Artifacts" 区域点击 `app-debug` 下载

**方式 B：从 Releases 下载**
1. 打开 https://github.com/ximiedeyanhuo/finance-tracker/releases
2. 下载最新的 Release 中的 APK

### 4. 安装到手机
```bash
adb install app-debug.apk
```

或通过微信/QQ 发送到手机安装

---

## 常见问题

### Q1: 提示 "Support for password authentication was removed"
**解决**：必须使用 Personal Access Token，不能使用 GitHub 密码
- 参考上面的 "获取 Personal Access Token" 章节

### Q2: 403 Permission denied
**解决**：
1. 确保仓库已创建
2. 确保你是仓库所有者或有写入权限
3. 检查 Token 权限是否正确

### Q3: 仓库不存在
**解决**：先在 GitHub 创建仓库
1. 打开 https://github.com/new
2. 仓库名：`finance-tracker`
3. 设为公开（Public）
4. 点击 "Create repository"

### Q4: 推送后 Actions 没有运行
**解决**：
1. 检查 `.github/workflows/android-ci.yml` 是否存在
2. 在仓库 Settings → Actions → General 中启用 Actions
3. 确保 workflow 文件语法正确

---

## 快速检查清单

- [ ] GitHub 仓库已创建
- [ ] Personal Access Token 已生成
- [ ] 代码已提交（已完成 ✅）
- [ ] 远程仓库已配置
- [ ] 推送到 GitHub
- [ ] Actions 编译开始
- [ ] 等待编译完成
- [ ] 下载 APK
- [ ] 安装测试

---

## 当前状态

✅ 代码已准备就绪
✅ Git 仓库已初始化
✅ 所有代码已提交
✅ GitHub Actions 配置完成
⏳ 等待推送到 GitHub

---

**下一步：执行推送命令**

```bash
cd /workspace/finance-tracker
./push-to-github.sh
```

或手动执行：
```bash
cd /workspace/finance-tracker
git remote add origin https://github.com/ximiedeyanhuo/finance-tracker.git
git branch -M main
git push -u origin main
# 输入用户名和 Personal Access Token
```
