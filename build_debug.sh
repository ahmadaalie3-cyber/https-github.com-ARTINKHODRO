#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if command -v gradle >/dev/null 2>&1; then
  gradle clean assembleDebug
elif [[ -x ./gradlew ]]; then
  ./gradlew clean assembleDebug
else
  echo "Gradle is missing. Open this project in Android Studio, install SDK Platform 35, sync Gradle, then Build > Build APK(s)." >&2
  exit 2
fi
printf '\nAPK: %s\n' "$PWD/app/build/outputs/apk/debug/app-debug.apk"
