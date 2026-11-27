# Employee Task Manager — Week 2 Day 9 Evidence

**Date:** 26 November 2026

## 1. Automated Tests
- **Gradle Task:** `./gradlew.bat :app:test`
- **Results:**
  - Total Tests: 25
  - Passed: 25
  - Failures: 0
  - Errors: 0
  - Skipped: 0
- **Status:** SUCCESSFUL.

## 2. APK Build & Verification
- **Gradle Task:** `./gradlew.bat :app:assembleDebug`
- **Status:** SUCCESSFUL.
- **APK Path:** `app/build/outputs/apk/debug/app-debug.apk`
- **APK Size:** 6,398,726 bytes.

## 3. Codex Review Findings & Resolution
- **Finding 1:** Duplicate JUnit dependency declaration in `app/build.gradle.kts`.
  - *Resolution:* Removed the duplicate `testImplementation(libs.junit)` line.
- **Finding 2:** Unused `import org.junit.Assert.assertTrue` in `TaskViewModelTest.kt`.
  - *Resolution:* Removed the unused import.
- *Note:* Subsequent tests and debug build re-run successfully. No second Codex review claimed.

## 4. Physical Device UI Verification
- **Device Model:** OPPO CPH1877
- **Device ID:** `82ac82ae`
- **Installation Method:** `adb install -r app/build/outputs/apk/debug/app-debug.apk` (Existing app data preserved).
- **Verification Results:**
  - Home / Task Details navigation: **PASS**
  - Create task with priority: **PASS**
  - Display task and completion status: **PASS**
  - All / Pending / Completed filters: **PASS**
  - Dashboard counts: **PASS**
  - Delete confirmation: **PASS**
  - REST API Loading / Success states: **PASS**
  - API Error and Retry: **PASS** (Verified via unit tests and exception handling in `TaskApiClient`).

## 5. Existing Screenshots
- `docs/week 2/day 9/screenshots/01-home-screen.png`
- `docs/week 2/day 9/screenshots/02-task-list-screen.png`
