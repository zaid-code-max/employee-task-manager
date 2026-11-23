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
