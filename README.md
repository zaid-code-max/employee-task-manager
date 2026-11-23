# Employee Task Manager

An Android application for managing employee tasks, built with MVVM and a repository boundary over Room. There is no separate domain or use-case layer.

---

## Project Overview

* **Application Name:** Employee Task Manager
* **Package Name:** `com.example.employeetaskmanager`
* **Programming Language:** Kotlin
* **Minimum SDK:** API 24 (Android 7.0 Nougat)
* **Target / Compile SDK:** API 37
* **Build System:** Gradle (Kotlin DSL `.gradle.kts`) with Version Catalog (`libs.versions.toml`)
* **AGP Version:** 9.5.2
* **Current behavior:** Room persistence, task priority, delete with confirmation, All / Pending / Completed filters, dashboard total / pending / completed counts, and one-way completion are implemented. The Day 3 checklist below is a historical acceptance record, not the full current feature list.

---

## Business Requirements

Employees must be able to:
1. **View Tasks:** View available tasks with a clear visual representation of their status.
2. **Add Task:** Create new tasks by entering task details with validation.
3. **Mark Complete:** Mark pending tasks as completed.
4. **View Status:** See whether a task is in "Pending" or "Completed" status.

Priority, delete, filters, dashboard counts, and one-way completion are implemented in the source in addition to those four requests. They are existing behavior, not a change to the original business request.

---

## Task Model

`TaskEntity` maps to the Room table `tasks`:

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Auto-generated primary key, default `0L` |
| `title` | `String` | Task text. Blank titles are rejected |
| `isCompleted` | `Boolean` | Default `false`. `false` is Pending. `true` is Completed |
| `priority` | `String` | Default `"Medium"`. The spinner offers Low, Medium, and High |

`AppDatabase` is the singleton database `task_database`, version 4, with `exportSchema = false` and `fallbackToDestructiveMigration(true)`.

---

## Implemented Features

* **Home navigation:** `MainActivity` starts `TaskDetailsActivity` with an explicit `Intent`. `MainActivity` has no ViewModel.
* **Task list:** `TaskDetailsActivity` shows tasks from `filteredTasks` through `TaskAdapter`.
* **Room local persistence:** `AppDatabase`, `TaskEntity`, and `TaskDao` keep tasks across screen changes and process restarts, subject to destructive migration on an older schema.
* **Add task:** The screen rejects a blank title with `R.string.error_empty_title`. `TaskViewModel.insertTask` trims the title and returns without inserting when the result is blank. New tasks start pending.
* **Priority:** Spinner values are Low, Medium, and High, starting on Medium. The DAO orders High, then Medium, then Low, then any other value, then pending before completed, then `id DESC`.
* **One-way completion:** `TaskAdapter` disables the checkbox after a task is completed, so the list cannot uncheck it. `TaskDao.updateTaskCompletion` still writes the Boolean it receives.
* **Status:** Pending and Completed come only from `isCompleted`.
* **Delete:** The screen shows a confirmation dialog, then calls `deleteTask(id)`, and shows the toast "Task deleted" when the user confirms.
* **Filters:** `ALL`, `PENDING`, and `COMPLETED` are applied in memory. The empty state follows the filtered list.
* **Dashboard statistics:** `TaskDetailsActivity` counts total, pending, and completed from `allTasks`, so the counts stay global while the list is filtered.
* **Reactive MVVM:** `TaskRepository.allTasks` is a cold `Flow`. `TaskViewModel` exposes `allTasks` and `filteredTasks` as `StateFlow`.

---

## Architecture

The project follows this MVVM repository flow:

```text
MainActivity (navigation only)
  -> TaskDetailsActivity
    -> TaskViewModelFactory
      -> AppDatabase -> TaskDao -> TaskRepository -> TaskViewModel
```

Runtime task operations follow:

```text
Screen (Activity) -> ViewModel -> Repository -> Room Storage (DAO / Database)
```

* **Screen (UI):** `MainActivity` navigates. `TaskDetailsActivity` handles input, confirmation, filter-button colors, empty state, dashboard counts, and collects ViewModel state.
* **ViewModel:** `TaskViewModel` exposes `StateFlow` and delegates `insertTask`, `updateTaskCompletion`, `deleteTask`, and `setFilter` to the repository.
* **Repository:** `TaskRepository` is the data-access boundary. `allTasks` is a cold `Flow` from `TaskDao.getAllTasks()`. The repository does not expose `StateFlow`.
* **Storage:** Room `AppDatabase` stores `TaskEntity` rows. The DAO query `Flow` re-emits after writes to `tasks`.

`TaskViewModel.allTasks` and `filteredTasks` use `stateIn` with `viewModelScope` and `SharingStarted.Eagerly`. Their initial value is `emptyList()`. `filteredTasks` combines the repository `Flow` with `currentFilter`. The activity collects `allTasks` and `filteredTasks` with `lifecycleScope` and `repeatOnLifecycle(STARTED)`. It does not collect `currentFilter`.

For a diagram of the earlier baseline flow, see [docs/diagrams/architecture.mmd](docs/diagrams/architecture.mmd). Current behavior is this README together with `AGENTS.md`.

---

## Recorded Technical Risks

These are recorded facts about the current code. This section does not request a behavior change.

* Database version 4 uses `fallbackToDestructiveMigration(true)` and does not export a schema. An older installed database is dropped on open.
* One-way completion is the disabled checkbox in `TaskAdapter`. The DAO update still accepts any Boolean.
* `priority` is an unconstrained `String`. Unknown values sort after Low and use the Medium color.
* The activity does not collect `currentFilter`, so filter-button colors can disagree with the saved filter after recreation.
* `allTasks` and `filteredTasks` each collect the cold Room `Flow`.
* The "Task deleted" toast is shown when the user confirms, before the database write finishes.
* The first `StateFlow` value is `emptyList()` until Room emits.
* `docs/ARCHITECTURE.md` and `docs/TASK_MODEL.md` describe an earlier model and can omit priority, delete, filters, and dashboard counts.

---

## Project Structure

```text
Employeetaskmanager/
├── .cursor/
│   └── rules/
│       └── employee-task-manager.mdc
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/employeetaskmanager/
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── TaskAdapter.kt
│   │   │   │   ├── TaskDao.kt
│   │   │   │   ├── TaskDetailsActivity.kt
│   │   │   │   ├── TaskEntity.kt
│   │   │   │   ├── TaskRepository.kt
│   │   │   │   ├── TaskViewModel.kt
│   │   │   │   └── TaskViewModelFactory.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_task_details.xml
│   │   │   │   │   ├── item_task.xml
│   │   │   │   │   └── layout.xml
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   │   └── java/com/example/employeetaskmanager/ExampleUnitTest.kt
│   │   └── androidTest/
│   │       └── java/com/example/employeetaskmanager/ExampleInstrumentedTest.kt
│   └── build.gradle.kts
├── docs/
│   ├── APPROVAL.md
│   ├── ARCHITECTURE.md
│   ├── CODEX_REVIEW.md
│   ├── CURRENT_STATUS.md
│   ├── DECISIONS.md
│   ├── DEBUGGING_LOG.md
│   ├── DEVELOPMENT_PLAN.md
│   ├── REQUIREMENTS.md
│   ├── STORAGE_DESIGN.md
│   ├── STORAGE_TECHNOLOGY_DECISION.md
│   ├── TASK_MODEL.md
│   └── diagrams/
│       └── architecture.mmd
├── AGENTS.md
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Verified Tech Stack & Dependencies (`libs.versions.toml`)

* **Kotlin & Core:**
  * `core-ktx` (v1.10.1)
  * `appcompat` (v1.6.1)
  * `material` (v1.10.0)
  * `recyclerview` (v1.3.2)
  * `activity-ktx` (v1.8.2)
  * `lifecycle-viewmodel-ktx` & `lifecycle-runtime-ktx` (v2.6.2)
* **Storage & Persistence:**
  * Room Runtime & KTX (v2.8.5)
  * Room Compiler (KSP v2.3.10)
* **Testing:**
  * JUnit (v4.13.2)
  * AndroidX JUnit (v1.1.5)
  * Espresso Core (v3.5.1)

---

## How to Build, Test, and Run

### Prerequisites
* **Android Studio:** Ladybug or newer recommended
* **JDK:** Java 11 or higher
* **Android SDK:** API 37 installed

### Command Line Build & Test Instructions (PowerShell)

1. **Navigate to project root directory:**
   ```powershell
   cd Employeetaskmanager
   ```

2. **Build Debug APK:**
   ```powershell
   .\gradlew.bat :app:assembleDebug
   ```
   * **APK Location:** `app/build/outputs/apk/debug/app-debug.apk`

3. **Run Unit Tests:**
   ```powershell
   .\gradlew.bat :app:test
   ```

4. **Install on Connected Device / Emulator:**
   ```powershell
   .\gradlew.bat :app:installDebug
   ```

---

## Day 3 Acceptance Criteria (Historical)

This checklist records the Day 3 submission. It is not the complete current feature list. Current behavior is the **Implemented Features** section above.

1. App launches successfully — **PASS**
2. Home → Task Details navigation works — **PASS**
3. Task Details screen displays correctly — **PASS**
4. Empty-task state appears when there are no tasks — **PASS**
5. Valid task can be added — **PASS**
6. Empty task input is rejected with appropriate error — **PASS**
7. Newly added task appears in task list with Pending status — **PASS**
8. A pending task can be marked Completed — **PASS**
9. Completed status is displayed correctly — **PASS**
10. Repeated completion of completed task is handled safely — **PASS**
11. Task data remains available after reopening screen — **PASS**
12. No existing Day 1/Day 2 functionality broken — **PASS**

---

## Troubleshooting & Setup Issues

For a detailed list of setup issues, PowerShell environment configurations, Git fixes, and troubleshooting steps, see [SETUP_ISSUES.md](./SETUP_ISSUES.md).

---

## License

This project is configured as a standard Android application.
