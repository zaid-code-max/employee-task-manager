# AI Handoff — Employee Task Manager

## Project identity and stack

- Android app: **Employee Task Manager**
- Package: `com.example.employeetaskmanager`
- Kotlin with Android XML Views, RecyclerView, Room, Kotlin Coroutines/Flow/StateFlow, AndroidX ViewModel, and `ViewModelProvider.Factory`.
- Gradle Kotlin DSL, Version Catalog, and KSP for Room code generation.
- Current documented SDKs: min API 24, compile/target API 37. README records AGP 9.5.2.
- No separate domain or use-case layer.

## Actual architecture and data flow

Runtime task operations follow:

```
TaskDetailsActivity / TaskAdapter
  -> TaskViewModel
    -> TaskRepository
      -> TaskDao
        -> AppDatabase / Room
```

`MainActivity` is navigation-only and opens `TaskDetailsActivity` with an explicit Intent. `TaskDetailsActivity` obtains the ViewModel through `TaskViewModelFactory`. The factory wires the singleton Room database, DAO, repository, and ViewModel; it is dependency wiring, not the task data-access boundary.

The activity renders UI, validates empty input, handles delete confirmation, filter-button colors, empty-state visibility, dashboard counts, and collects ViewModel state. The ViewModel owns task UI state and commands. The repository is the sole data-access boundary. Room storage is confined to the DAO/database layer.

## Important Kotlin files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Home screen; explicit navigation to Task Details only. |
| `TaskDetailsActivity.kt` | Add-task controls and validation feedback, delete confirmation, filter controls, dashboard counts, and lifecycle-aware collection of task states. |
| `TaskViewModel.kt` | Injected repository; all/filtered task StateFlows, current filter, and insert/completion/delete/filter commands. |
| `TaskRepository.kt` | Forwards Room task Flow and suspend insert/update/delete operations. |
| `TaskDao.kt` | Room query and persistence operations; query orders priority, completion state, and ID. |
| `TaskEntity.kt` | Room entity for the `tasks` table. |
| `TaskAdapter.kt` | RecyclerView binding for title, priority, status, one-way checkbox, and delete callback. |
| `TaskViewModelFactory.kt` | Creates database/DAO/repository dependencies and injects repository into ViewModel. |
| `AppDatabase.kt` | Singleton Room database named `task_database`; version 4, schema export disabled, destructive migration fallback enabled. |

## Existing features to preserve

- Room local persistence.
- Task creation and viewing, including blank-title rejection.
- Priority selection (Low, Medium, High; Medium default) and priority-based sorting.
- Delete task with confirmation.
- All / Pending / Completed filters.
- Dashboard total, pending, and completed statistics.
- One-way completion: completed checkboxes are disabled in the list.
- Pending / Completed status derived from `isCompleted`.
- Home to Task Details navigation.

Priority sorting is High, Medium, Low, then other values; pending before completed; newest ID first within ties. Priority is stored as an unconstrained String.

## TaskEntity fields

- `id: Long = 0L` — auto-generated primary key.
- `title: String` — task title.
- `isCompleted: Boolean = false` — false means Pending; true means Completed.
- `priority: String = "Medium"` — database value is an unconstrained string.

## Flow and state behavior

- `TaskDao.getAllTasks()` returns a cold `Flow<List<TaskEntity>>`; Room re-emits after task-table changes.
- `TaskRepository.allTasks` forwards that same cold Flow; repository does not convert it to StateFlow.
- `TaskViewModel.allTasks` and `filteredTasks` are hot StateFlows built with `stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())`.
- `filteredTasks` combines the repository Flow with the current-filter MutableStateFlow. The default filter is ALL.
- The two ViewModel StateFlows separately collect the cold repository Flow.
- `TaskDetailsActivity` collects both in `lifecycleScope` / `repeatOnLifecycle(STARTED)`. It does not collect `currentFilter`; button appearance can diverge from the retained filter after recreation.
- Initial StateFlow values may be empty before Room emits stored rows.
- One-way completion is a UI rule. The DAO completion update accepts either Boolean.

## Git branch and current worktree

Current branch observed during this handoff: `training/agent-context`.

The following working-tree changes are uncommitted and must be preserved:

- Modified: `.cursor/rules/employee-task-manager.mdc`
- Modified: `AGENTS.md`
- Modified: `README.md`
- Modified: `docs/CURRENT_STATUS.md`
- Modified: `gradle/libs.versions.toml`
- Deleted: `docs/day6/DAY_6_EVIDENCE.md`
- Untracked: `AI_HANDOFF.md` (this handoff file itself; it is now untracked and was not present before it was created)
- Untracked: `docs/week 1/day 6/`

Do not infer authorship or commit status beyond this verified inventory.

## Last known build, test, and installation evidence

These are historical records in `docs/CURRENT_STATUS.md`, not checks rerun for this handoff:

- **November 20, 2026:** `.gradlew.bat clean assembleDebug` recorded successful.
- **November 20, 2026:** `.gradlew.bat testDebugUnitTest` recorded successful.
- **November 20, 2026:** `.gradlew.bat installDebug` recorded successful; installed on device `CPH1877 - 10`, with user confirmation that the app worked.
- README also records `.gradlew.bat :app:assembleDebug` as successful, but the status file does not assign a separate date to that command.

These results are historical and do not establish the current branch's build/test/device status. No build, test, or installation was rerun during this handoff. Located tests are generic template tests, not task-specific coverage.

## Codex review findings

The independent read-only review identified the following confirmed behaviors and coverage gaps:

1. **Delete feedback precedes database completion.** After confirmation, the activity calls the asynchronous ViewModel delete command and immediately displays “Task deleted.” A failure or delay could leave the task present after success feedback.
2. **Destructive migration can lose data.** Database version 4 has `exportSchema = false` and `fallbackToDestructiveMigration(true)`; an unsupported older schema can be dropped and recreated.
3. **Task-specific unit tests are missing.** Located local and instrumented tests only test integer addition and application package name.
4. **Filter-button appearance may diverge after recreation.** The Activity updates button colors in click listeners but does not collect `currentFilter`.

These risks are recorded for awareness. This handoff does not implement fixes or change behavior.

## Documentation map

- `README.md` — project overview, current features, architecture, task model, technical facts, build/test instructions, and historical acceptance record.
- `AGENTS.md` — authoritative current project architecture, behavior, and contributor/agent constraints.
- `.cursor/rules/employee-task-manager.mdc` — project-wide Cursor rules aligned with current source.
- `docs/CURRENT_STATUS.md` — current feature summary plus explicitly historical verification and notes.
- `docs/ARCHITECTURE.md` — architecture responsibilities and flows; some details describe the earlier baseline, so follow current Kotlin and AGENTS.md where they differ.
- `docs/REQUIREMENTS.md` — business user stories, acceptance criteria, and edge cases.
- `docs/DEVELOPMENT_PLAN.md` — original planned architecture, implementation order, testing plan, and change-control notes; it is planning documentation, not a full inventory of current features.
- `docs/DEBUGGING_LOG.md` — dated troubleshooting history, fixes, verification records, and explicitly pending/unconfirmed investigation notes.
- Other references listed in README/AGENTS include `docs/DECISIONS.md`, `docs/diagrams/architecture.mmd`, and historical day-scoped documents. Day-scoped notes may omit later implemented features.

## Next recommended task

Validate agent context in a fresh session. Ask the new agent to summarize the architecture, current worktree changes, preserved features, known review findings, and verification limitations. Check that it understands these points before allowing any implementation. Do not treat this handoff as authorization to modify existing work.
