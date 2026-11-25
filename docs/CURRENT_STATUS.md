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

`TaskDao.getAllTasks()` emits a cold `Flow`. `TaskRepository.allTasks` forwards that `Flow`. `TaskViewModel` exposes `allTasks` and `filteredTasks` as `StateFlow`. `TaskDetailsActivity` collects both. `MainActivity` only navigates to Task Details and has no ViewModel.

The Day 4 and Day 6 notes below are historical. They are not two competing descriptions of the current tree.

## Historical notes

### Day 6 Integration & Main Branch Merge

Historical merge note. This section reports what that merge claimed. It does not by itself mention dashboard statistics. The **Current application** section is the statement that those counts are in the source.

- Successfully merged `feature/task-list` into `main`.
- That merge note reports Room persistence, Task Priority, Delete Task, One-Way Completion, and Filter Tabs as live on the main branch repository.
- Verified all builds, unit tests, and device deployment for that submission.

### Day 3 Baseline

Historical baseline for that day:

- Room storage, Repository, ViewModel, and ViewModelFactory.
- Task creation, viewing, completion, status display, empty state, and input validation.
- Home to Task Details navigation.
- Approved architecture: `Screen -> ViewModel -> Repository -> Storage`.

### Day 4 - Task List Screen Only

Historical exercise snapshot for that day. The removal bullets describe that day's scope. They do not describe the current application. Later source, including the Day 6 merge note and the current Kotlin files, includes priority, delete, one-way completion, filter tabs, and dashboard statistics.

- Implemented the Task List screen UI.
- Kept task creation, task list display, completion checkbox, Pending/Completed status, and empty state.
- Kept the existing Home to Task List navigation.
- Kept the existing local Room storage from the approved Day 3 plan.
- For that Day 4 exercise, removed out-of-scope Priority, Delete, dashboard statistics, database migration additions, CI workflow, and Home Screen redesign.

### Final Submission & Verification Update

Historical record. Date: November 20, 2026.

- All Friday submission requirements verified successfully for that submission.
- APK build, unit tests, and device deployment verified for that submission.

## Historical verification

These results were recorded earlier. This documentation sync did not re-run them and does not add a new pass or fail claim.

- `.\gradlew.bat clean assembleDebug` - BUILD SUCCESSFUL.
- `.\gradlew.bat testDebugUnitTest` - BUILD SUCCESSFUL.
- `.\gradlew.bat installDebug` - BUILD SUCCESSFUL.
- APK installed on connected device `CPH1877 - 10`.
- User confirmed that the app is working after installation.
- README build command `.\gradlew.bat :app:assembleDebug` - BUILD SUCCESSFUL.

## Recorded technical risks

Recording these facts does not authorize a behavior change.

- `AppDatabase` is version 4, `exportSchema = false`, and uses `fallbackToDestructiveMigration(true)`.
- One-way completion is enforced by disabling the checkbox. `updateTaskCompletion` still accepts any Boolean.
- `TaskDetailsActivity` does not collect `currentFilter`, so filter-button highlight can diverge from the stored filter after recreation.
- `allTasks` and `filteredTasks` are two separate collections of the cold Room `Flow`.
- Day-scoped documents such as `docs/ARCHITECTURE.md` and `docs/TASK_MODEL.md` still describe an earlier model and can omit priority, delete, filters, and dashboard counts. `AGENTS.md` and `.cursor/rules/employee-task-manager.mdc` describe the current implementation.

## Remaining

- Application code delivery is complete.
- This documentation sync aligns `AGENTS.md`, the Cursor project rule, this status file, and `README.md` with the current source. It is not a new feature.

## Week 2 Day 7 — REST API Task Retrieval

**Status:** Implementation and network evidence captured.

### Implemented components

- Added `TaskApiClient` using `HttpURLConnection` for GET requests.
- Integrated JSONPlaceholder endpoint: `GET https://jsonplaceholder.typicode.com/todos`.
- Added `ApiTaskDto` with userId, id, title, and completed fields.
- Integrated API retrieval through `TaskRepository.fetchApiTasks()`.
- Added API UI states in `TaskViewModel`: Idle, Loading, Success, Empty, and Error.
- Added Retry using `loadApiTasks(forceRefresh = true)`.
- Added an independent API task RecyclerView using `ApiTaskAdapter`.
- Preserved the existing local Room task list and dashboard.

### Network verification evidence

- Android Studio Network Inspector captured three GET `/todos` requests.
- All three displayed HTTP 200.
- JSON response body was confirmed to contain 200 task records.
- Device: OPPO CPH1877.
- The API task list displayed remote task information separately from local Room tasks.

### Saved evidence

Location: `docs/week 2/day 7/screenshots/`

1. `01-network-request-http-200.png`
2. `02-network-response-200-records.png`
3. `03-api-task-list-200-records.jpg`

### Architecture documentation

- Updated `docs/diagrams/api_task_retrieval_architecture.mmd`.
- Updated `docs/diagrams/api_task_retrieval_sequence.mmd`.
- Preserved the original `docs/diagrams/architecture.mmd`.

### API limitations

- Remote API records are read-only.
- No Room persistence, API caching, or offline synchronization is implemented.

## Week 2 Day 7 - Verification Record Clarification

### Authoritative API verification record

- `docs/API_CONTRACT.md` is the detailed REST API contract and verification record.
- Previously recorded unit-test, build, installation, Airplane Mode, and retry-recovery results remain historical verification records.
- Those historical build and test results were not rerun during the current Day 7 documentation update.

### Newly captured Network Inspector evidence

Date: 24 November 2026.

- Device: OPPO CPH1877.
- Three `GET /todos` requests were captured in Android Studio Network Inspector.
- All three displayed HTTP 200.
- Response Body was confirmed to contain 200 JSON records.
- Three supporting screenshots were saved in `docs/week 2/day 7/screenshots/`.

### Independent Codex review

- Recommendation: APPROVE WITH MINOR FOLLOW-UP.
- No critical or major findings identified.
- No confirmed bugs established by the read-only review.
- Remaining observations are recorded in `docs/CODEX_REVIEW.md`.
