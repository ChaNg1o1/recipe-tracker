@echo off
chcp 65001 >nul

echo.

java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java 17环境
    pause
    exit /b 1
)

mvn -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Maven
    pause
    exit /b 1
)

echo [1/3] 正在编译项目...
call mvn clean compile 2>&1 | findstr /V "WARNING:"
if errorlevel 1 (
    echo.
    echo [错误] 编译失败！
    pause
    exit /b 1
)

echo.
echo [2/3] 编译成功
echo [3/3] 正在启动...
echo.
echo.

call mvn exec:java -Dexec.mainClass="com.chang1o.recipe.Main" 2>&1 | findstr /V "WARNING:"

echo.
echo 已退出
pause
