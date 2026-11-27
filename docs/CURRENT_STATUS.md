# Employee Task Manager - Current Status

## Current application

The Kotlin sources are the current behavior. These features are present:

- Room persistence through `AppDatabase`, `TaskEntity`, `TaskDao`, and `TaskRepository`.
- Task fields on `TaskEntity`: `id` (`Long`, auto-generated), `title` (`String`), `isCompleted` (`Boolean`, default `false`), and `priority` (`String`, default `"Medium"`).
- Priority, including the Low / Medium / High spinner and SQL ordering by priority, then pending before completed, then `id DESC`.
- Delete with a confirmation dialog, then `TaskViewModel.deleteTask`.
- One-way completion: `TaskAdapter` disables the checkbox when a task is completed. `TaskDao.updateTaskCompletion` still writes the Boolean it receives.
- Filter tabs: `ALL`, `PENDING`, and `COMPLETED`, applied in memory by `TaskViewModel`.
- Dashboard statistics: `TaskDetailsActivity` counts total, pending, and completed from `allTasks`.
- REST API integration via `TaskApiClient` and `ApiTaskDto`.

`TaskDao.getAllTasks()` emits a cold `Flow`. `TaskRepository.allTasks` forwards that `Flow`. `TaskViewModel` exposes `allTasks` and `filteredTasks` as `StateFlow`. `TaskDetailsActivity` collects both. `MainActivity` only navigates to Task Details and has no ViewModel.

---

## Week 2 Day 9 Status & Verification (26 November 2026)

### Automated Tests
- Gradle task: `./gradlew.bat :app:test`
- Total Tests: 25
- Passed: 25
- Failures: 0
- Errors: 0
- Skipped: 0
- Status: SUCCESSFUL.

### APK Build & Verification
- Gradle task: `./gradlew.bat :app:assembleDebug`
- Status: SUCCESSFUL.
- APK Path: `app/build/outputs/apk/debug/app-debug.apk`
- Size: 6,398,726 bytes.

### Codex Review Fixes
- Duplicate JUnit dependency in `app/build.gradle.kts` removed.
- Unused `assertTrue` import in `TaskViewModelTest.kt` removed.
- Subsequent unit tests and build re-run successfully.

### Physical Device UI Verification
- Device Model: OPPO CPH1877 (`82ac82ae`).
- Installation: `adb install -r` (Existing app data preserved).
- Home / Task Details navigation: PASS.
- Create task with priority: PASS.
- Display task and completion status: PASS.
- All / Pending / Completed filters: PASS.
- Dashboard counts: PASS.
- Delete confirmation: PASS.
- REST API Loading / Success states: PASS.
- API Error / Retry: PASS.

### Captured Screenshots
- `docs/week 2/day 9/screenshots/01-home-screen.png`
- `docs/week 2/day 9/screenshots/02-task-list-screen.png`
