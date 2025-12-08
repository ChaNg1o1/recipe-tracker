# RecipeTracker {{projectVersion}}


#### Linux (x86_64)
```bash
# 下载
wget https://github.com/ChaNg1o1/RecipeTracker/releases/download/v{{projectVersion}}/recipe-tracker-linux-amd64

# 设置可执行权限
chmod +x recipe-tracker-linux-amd64

# 运行
./recipe-tracker-linux-amd64
```

#### macOS (Intel)
```bash
# 下载
curl -LO https://github.com/ChaNg1o1/RecipeTracker/releases/download/v{{projectVersion}}/recipe-tracker-macos-amd64

# 设置可执行权限
chmod +x recipe-tracker-macos-amd64

# 运行 (您可能需要在系统偏好设置 > 安全性和隐私 中允许运行)
./recipe-tracker-macos-amd64
```

#### macOS (Apple Silicon)
```bash
# 下载
curl -LO https://github.com/ChaNg1o1/RecipeTracker/releases/download/v{{projectVersion}}/recipe-tracker-macos-arm64

# 设置可执行权限
chmod +x recipe-tracker-macos-arm64

# 运行 (您可能需要在系统偏好设置 > 安全性和隐私 中允许运行)
./recipe-tracker-macos-arm64
```

#### Windows (x86_64)
```powershell
# 使用 PowerShell 下载
Invoke-WebRequest -Uri "https://github.com/ChaNg1o1/RecipeTracker/releases/download/v{{projectVersion}}/recipe-tracker-windows-amd64.exe" -OutFile "recipe-tracker.exe"

# 运行
.\recipe-tracker.exe
```

## 更改内容