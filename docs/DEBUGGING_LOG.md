# Employee Task Manager - Debugging Log

This document records observed issues, investigation results, fixes, and verification. Unverified root causes or tests are marked as pending rather than assumed.

For environment setup and general troubleshooting, refer to `SETUP_ISSUES.md`.

---

## 1. KSP Generated Source Registration Error

**Date:** 17 November 2026

**Category:** Gradle / Kotlin Symbol Processing

### Symptom
Gradle configuration failed after adding Room and KSP dependencies.

The build reported:

`Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin.`

### Cause
The initial KSP plugin version was `2.2.10-2.0.2`. In the configured AGP 9 built-in Kotlin environment, this setup triggered an attempt to register generated Kotlin source directories through the `kotlin.sourceSets` DSL.

### Resolution
Updated the KSP version in `gradle/libs.versions.toml` to `2.3.10`. No `android.disallowKotlinSourceSets=false` workaround was added.

### Verification
- Gradle Sync: Successful.
- Gradle Build: Successful.
- KSP code generation: Verified.

---

## 2. TaskViewModel Direct Storage Dependency

**Date:** 17 November 2026

**Category:** Architecture Review

### Finding
The initial `TaskViewModel` implementation directly created `AppDatabase` and accessed `TaskDao`, violating the approved architecture boundary:

`Screen -> ViewModel -> Repository -> Storage`

### Resolution
- Changed `TaskViewModel` to extend `ViewModel`.
- Injected `TaskRepository` through the constructor.
- Created `TaskViewModelFactory`.
- Moved database, DAO, and Repository construction into the factory.
- Updated `TaskDetailsActivity` to obtain the ViewModel through the factory.

### Verification
- ViewModel and Activity boundary inspection: Passed.
- Gradle Build: Successful.
- Unit Tests: 1 passed, 0 skipped, 0 failed.

---

## 3. Day 4 Controlled Bug Challenges

**Scope:** Task List screen only, as required by the Day 4 slide.

### Bug A — Button tap does nothing

**Reported behavior:** A button tap does not trigger its intended action.

**Inspection:** The Home navigation button already has a click listener in `MainActivity`. The Task List Add Task button also has a click listener in `TaskDetailsActivity`.

**Current finding:** The slide does not identify which button instance failed. The existing device notes reported that Home to Task List navigation worked, but a breakpoint trace and a reproduced failure were not captured in the available evidence.

**Root cause:** Not yet confirmed.

**Fix / retest:** No speculative code change has been made to the Home navigation handler. Reproduce the exact failing button, inspect its view ID/listener with a breakpoint, then record and retest the verified fix.

### Bug B — Task list fails to display or update correctly

**Reported behavior:** Task records do not display or refresh correctly.

**Code change retained:** `TaskViewModel` now uses `SharingStarted.Eagerly` for its Room-backed task Flow so the ViewModel starts collecting task updates for its lifetime.

**Current finding:** Earlier device notes reported that a newly added task appeared immediately. However, the original failure trace and the exact root cause were not captured.

**Root cause:** Not yet confirmed.

**Fix / retest:** The Flow collection change is retained as the current correction attempt. Reproduce the original issue, inspect the emitted task list and adapter submission with breakpoints, and verify add/completion updates after this scope reduction.

### Post-cleanup verification
- Clean Debug APK build: Pending.
- Unit tests: Pending.
- Device retest for Bug A: Pending.
- Device retest for Bug B: Pending.
