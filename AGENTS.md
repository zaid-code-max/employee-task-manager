# AGENTS.md — Employee Task Manager Guidelines

This document defines the architecture, coding standards, and development rules for contributors and AI coding agents working on the Employee Task Manager project.

---

## 1. Project Overview

- Application: Employee Task Manager.
- Platform: Android.
- Programming language: Kotlin.
- Package name: `com.example.employeetaskmanager`.
- Business request: Employees must be able to view tasks, add tasks, mark complete, and see status.

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
Screen (Activity) -> ViewModel -> Repository -> Storage (Room)
```

Responsibilities:
- Screen (Activity): Handles UI rendering, user input, and collects state from ViewModel.
- ViewModel: Manages UI state, handles user actions, and requests operations from Repository.
- Repository: Acts as a single data-access boundary between ViewModel and Storage.
- Storage (Room): Provides SQLite database persistence via AppDatabase and TaskDao.

## 4. Strict Architecture Rules

- Activities must communicate with TaskViewModel for all task operations.
- TaskViewModel must receive TaskRepository through constructor injection.
- TaskViewModel must not directly access AppDatabase, TaskDao, Room, or Application.
- TaskViewModelFactory is responsible for constructing AppDatabase, obtaining TaskDao, creating TaskRepository, and supplying it to TaskViewModel.
- TaskRepository is the sole data-access boundary.
- Room access must remain strictly inside the Storage/Repository boundary.
- Do not bypass the approved architecture.

## 5. Existing Task Model

- id: Long (auto-generated primary key).
- title: String (task description).
- isCompleted: Boolean (completion status).
- New tasks start as pending (`isCompleted = false`).
- Empty task titles are invalid.
- Task status is derived from `isCompleted`.

## 6. Existing Functionality

- Task viewing via reactive StateFlow and RecyclerView ListAdapter.
- Task creation with empty-input validation.
- Task completion toggle.
- Status display (Pending vs Completed).
- Empty-state handling.
- Local Room database persistence.
- Home to Task Details navigation via explicit Intent.

## 7. Development Rules

- Inspect relevant files before modifying them.
- Follow existing naming and coding conventions.
- Preserve Day 1 and Day 2 functionality.
- Make minimal, incremental changes.
- Do not introduce unrelated dependencies or features.
- Do not invent business requirements.
- Do not claim tests pass without actually running them.
- Update documentation when architecture or behavior changes.
- Do not modify unrelated files.
- Do not commit or push unless explicitly requested.

## 8. Build and Test Commands

Use Windows PowerShell commands:

```powershell
.\gradlew.bat :app:assembleDebug
```

```powershell
.\gradlew.bat :app:test
```

## 9. Documentation References

Reference existing project documentation:
- README.md
- docs/REQUIREMENTS.md
- docs/ARCHITECTURE.md
- docs/DEVELOPMENT_PLAN.md
- docs/DECISIONS.md
- docs/CURRENT_STATUS.md
- docs/diagrams/architecture.mmd
