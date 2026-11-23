# AGENTS.md — Employee Task Manager Guidelines

This document defines the architecture, coding standards, and development rules for contributors and AI coding agents working on the Employee Task Manager project.

This file describes the current implementation. Day-scoped documents under `docs/` are historical and can omit later features. When those documents disagree with this file, follow this file and the current Kotlin sources.

---

## 1. Project Overview

- Application: Employee Task Manager.
- Platform: Android.
- Programming language: Kotlin.
- Package name: `com.example.employeetaskmanager`.
- Business request: Employees must be able to view tasks, add tasks, mark complete, and see status.

Implemented behavior already in the source, beyond that original request: task priority, delete with confirmation, All / Pending / Completed filters, dashboard total / pending / completed counts, and one-way completion. These are existing code, not new business requirements.

## 2. Technology Stack

- Kotlin.
- Android XML Views.
- RecyclerView.
- Room Persistence Library for local database storage.
- Kotlin Coroutines, Flow, and StateFlow.
- AndroidX ViewModel.
- ViewModelProvider.Factory.
- Gradle Kotlin DSL.
- Gradle Version Catalog: `gradle/libs.versions.toml`.
- KSP for Room code generation.

## 3. Approved Architecture

The approved application architecture is:

```text
MainActivity (navigation only)
  -> TaskDetailsActivity
    -> TaskViewModelFactory
      -> AppDatabase -> TaskDao -> TaskRepository -> TaskViewModel
```

Runtime task operations follow:

```text
Screen (Activity) -> ViewModel -> Repository -> Storage (Room)
```

Responsibilities:
- Screen (Activity): Handles UI rendering, user input, and collects state from ViewModel. `MainActivity` only starts `TaskDetailsActivity` and has no ViewModel.
- ViewModel: Manages UI state, handles user actions, and requests operations from Repository.
- Repository: Acts as a single data-access boundary between ViewModel and Storage. It exposes a cold `Flow` and forwards suspend writes.
- Storage (Room): Provides SQLite database persistence via `AppDatabase` and `TaskDao`. `TaskDao.getAllTasks()` emits a cold `Flow`.

`TaskViewModelFactory` builds `AppDatabase`, obtains `TaskDao`, creates `TaskRepository`, and passes that repository to `TaskViewModel`. The factory is dependency wiring. It is not the data-access boundary for task operations.

## 4. Strict Architecture Rules

- Activities must communicate with `TaskViewModel` for all task operations.
- `TaskViewModel` must receive `TaskRepository` through constructor injection.
- `TaskViewModel` must not directly access `AppDatabase`, `TaskDao`, Room, or `Application`.
- `TaskViewModelFactory` is responsible for constructing `AppDatabase`, obtaining `TaskDao`, creating `TaskRepository`, and supplying it to `TaskViewModel`.
- `TaskRepository` is the sole data-access boundary.
- Room access must remain strictly inside the Storage/Repository boundary.
- Do not bypass the approved architecture.
- Task commands on the ViewModel are `insertTask`, `updateTaskCompletion`, `deleteTask`, and `setFilter`.

## 5. Existing Task Model

`TaskEntity` maps to the Room table `tasks`:

- `id`: `Long`, auto-generated primary key, default `0L`.
- `title`: `String`.
- `isCompleted`: `Boolean`, default `false`.
- `priority`: `String`, default `"Medium"`.

Status comes only from `isCompleted`:

- `isCompleted = false` means Pending.
- `isCompleted = true` means Completed.

Rules:

- Task title must not be empty or blank.
- New tasks start as pending (`isCompleted = false`).
- The add-task spinner offers `Low`, `Medium`, and `High`, and starts on `Medium`. Those are UI values. The database stores `priority` as an unconstrained `String`.
- Empty task titles are rejected in `TaskDetailsActivity` with a toast using `R.string.error_empty_title`. `TaskViewModel.insertTask` trims the title and returns without inserting when the result is blank.

## 6. Existing Functionality

- Home to Task Details navigation via an explicit `Intent` from `MainActivity`. `MainActivity` has no ViewModel.
- Local Room persistence in the singleton database `task_database`.
- Task viewing through `filteredTasks` and a RecyclerView `ListAdapter`.
- Task creation with the empty-title checks described above.
- Priority display and SQL ordering: High, then Medium, then Low, then any other value, then pending before completed, then `id DESC`.
- One-way completion on screen: `TaskAdapter` disables the checkbox when `isCompleted` is true, so a completed task cannot be unchecked from the list. `TaskDao.updateTaskCompletion` still writes the Boolean it receives.
- Status display (Pending vs Completed) from `isCompleted`.
- Delete: `TaskDetailsActivity` shows a confirmation dialog, then calls `deleteTask(id)`. The activity shows the toast "Task deleted" when the user confirms.
- Filters: `TaskFilter` values `ALL`, `PENDING`, and `COMPLETED`, applied in memory by `combine` of the repository task `Flow` and `currentFilter`.
- Dashboard statistics: `TaskDetailsActivity` counts total, pending, and completed from `allTasks`. Those counts stay global while the list is filtered.
- Empty-state handling follows the filtered list. A filter with no matching rows shows the empty state.

## 7. Room, Flow, and StateFlow

- `TaskDao.getAllTasks()` returns a cold `Flow<List<TaskEntity>>`. Room re-emits after writes to `tasks`.
- `TaskRepository.allTasks` is that same cold `Flow`. The repository does not convert it to `StateFlow`.
- `TaskViewModel.allTasks` and `TaskViewModel.filteredTasks` are hot `StateFlow`s created with `stateIn`, `viewModelScope`, and `SharingStarted.Eagerly`. Their initial value is `emptyList()`.
- `filteredTasks` uses `combine(repository.allTasks, _currentFilter)`.
- `currentFilter` is a `StateFlow` backed by a `MutableStateFlow`, default `TaskFilter.ALL`. `setFilter` writes that value.
- `allTasks` and `filteredTasks` each subscribe to the cold repository `Flow`.
- `TaskDetailsActivity` collects `allTasks` and `filteredTasks` with `lifecycleScope` and `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- The activity does not collect `currentFilter`. Filter-button colors are updated in the click listeners.

## 8. Recorded Technical Behavior

Record these facts. Do not change them unless a later request explicitly asks for a behavior change.

- `AppDatabase` is version 4, `exportSchema = false`, and uses `fallbackToDestructiveMigration(true)`.
- `allTasks` and `filteredTasks` are two separate collections of the cold Room `Flow`.
- The "Task deleted" toast is shown when the user confirms, before the database write finishes.
- `priority` is stored as an unconstrained `String`.
- Filter-button highlight can diverge from `currentFilter` after recreation, because the activity does not collect `currentFilter`.
- The first `StateFlow` value can be an empty list, before Room emits stored rows.
- Day-scoped documents under `docs/` can omit priority, delete, filters, dashboard counts, and one-way completion.

## 9. Development Rules

- Inspect relevant files before modifying them.
- Follow existing naming and coding conventions.
- Preserve Day 1 and Day 2 functionality.
- Preserve existing priority, delete, filter, dashboard statistics, and one-way completion behavior. Do not remove those features.
- Make minimal, incremental changes.
- Do not introduce unrelated dependencies or features.
- Do not invent business requirements.
- Do not claim tests pass without actually running them.
- Update documentation when architecture or behavior changes.
- Do not modify unrelated files.
- Do not commit or push unless explicitly requested.

## 10. Build and Test Commands

Use Windows PowerShell commands:

```powershell
.\gradlew.bat :app:assembleDebug
```

```powershell
.\gradlew.bat :app:test
```

## 11. Documentation References

Reference existing project documentation:
- README.md
- docs/REQUIREMENTS.md
- docs/ARCHITECTURE.md
- docs/DEVELOPMENT_PLAN.md
- docs/DECISIONS.md
- docs/CURRENT_STATUS.md
- docs/diagrams/architecture.mmd

`docs/ARCHITECTURE.md`, `docs/TASK_MODEL.md`, and other day-scoped notes can describe an earlier baseline. This file and the current Kotlin sources win when they disagree.
