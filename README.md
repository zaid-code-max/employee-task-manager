# Employee Task Manager

An Android application designed for managing employee tasks and workflow tracking, built following modern Android architecture guidelines (MVVM & Clean Architecture with Room persistence).

---

## Project Overview

* **Application Name:** Employee Task Manager
* **Package Name:** `com.example.employeetaskmanager`
* **Programming Language:** Kotlin
* **Minimum SDK:** API 24 (Android 7.0 Nougat)
* **Target / Compile SDK:** API 37
* **Build System:** Gradle (Kotlin DSL `.gradle.kts`) with Version Catalog (`libs.versions.toml`)
* **AGP Version:** 9.5.1
* **Current Status:** Day 3 Complete (12/12 Acceptance Criteria Verified)

---

## Business Requirements

Employees must be able to:
1. **View Tasks:** View available tasks with a clear visual representation of their status.
2. **Add Task:** Create new tasks by entering task details with validation.
3. **Mark Complete:** Mark pending tasks as completed.
4. **View Status:** See whether a task is in "Pending" or "Completed" status.

---

## Implemented Features

* **Task Management Dashboard:** Dedicated Task Details screen supporting viewing, adding, and managing tasks.
* **Room Local Persistence:** SQLite database persistence (`AppDatabase`, `TaskEntity`, `TaskDao`) ensuring task data survives activity lifecycle changes and app restarts.
* **Reactive MVVM Architecture:** `TaskViewModel` observing `TaskRepository` data streams (`StateFlow`) using Kotlin Coroutines.
* **Input Validation:** Prevents creation of empty tasks with user-friendly feedback (`Toast`).
* **Empty-State Handling:** Clear empty-task state indication when no tasks exist.
* **Task Status Tracking:** Dynamic color-coded status labels ("Pending" / "Completed") with CheckBox state synchronization.

---

## Architecture

The project follows an approved Clean Architecture / MVVM flow:

```text
Screen (Activity) -> ViewModel -> Repository -> Room Storage (DAO / Database)
```

* **Screen (UI):** `MainActivity` & `TaskDetailsActivity` handle user interaction and observe UI state.
* **ViewModel:** `TaskViewModel` manages UI state and delegates operations to the repository.
* **Repository:** `TaskRepository` acts as the single data access boundary.
* **Storage:** Room Database (`AppDatabase`) stores persistent task data.

For visual architectural reference, see [docs/diagrams/architecture.mmd](docs/diagrams/architecture.mmd).

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

## Day 3 Acceptance Criteria Verification (12 / 12 PASS)

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
