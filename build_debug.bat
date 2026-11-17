@echo off
setlocal
cd /d "%~dp0"
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle was not found. Open this folder in Android Studio, install Android SDK Platform 35, sync Gradle, then Build ^> Build APK(s).
  pause
  exit /b 2
)
gradle clean assembleDebug
if errorlevel 1 exit /b %errorlevel%
echo.
echo APK: app\build\outputs\apk\debug\app-debug.apk
pause
