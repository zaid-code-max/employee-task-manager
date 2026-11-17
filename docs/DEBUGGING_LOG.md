# Employee Task Manager - Debugging Log

This document records actual implementation issues, their causes, resolutions, and verification results encountered during Employee Task Manager development.

For environment setup and general troubleshooting, refer to `SETUP_ISSUES.md`.

---

## 1. KSP Generated Source Registration Error

**Date:** 17 November 2026

**Category:** Gradle / Kotlin Symbol Processing

### Symptom

Gradle configuration failed after adding Room and KSP dependencies.

The build reported:

`Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin.`

The error identified KSP-generated source directories under:

`app/build/generated/ksp/debug/`

### Cause

The initial KSP plugin version was `2.2.10-2.0.2`.

In the configured AGP 9 built-in Kotlin environment, this setup triggered an attempt to register generated Kotlin source directories through the `kotlin.sourceSets` DSL, which was not allowed.

### Resolution

Updated the KSP version in `gradle/libs.versions.toml`:

- Previous version: `2.2.10-2.0.2`
- Selected version: `2.3.10`

No `android.disallowKotlinSourceSets=false` workaround was added.

### Verification

- Gradle Sync: Successful.
- Gradle Build: Successful.
- KSP code generation: Verified.

---

## 2. TaskViewModel Direct Storage Dependency

**Date:** 17 November 2026

**Category:** Architecture Review

### Finding

During architecture diagram preparation, source inspection identified that the initial `TaskViewModel` implementation directly created `AppDatabase` and accessed `TaskDao`.

This violated the approved architecture boundary:

`Screen -> ViewModel -> Repository -> Storage`

### Impact

The ViewModel was directly dependent on the Storage implementation instead of receiving the Repository through constructor injection.

This was an architecture boundary issue, not a reported application build failure.

### Resolution

The implementation was corrected as follows:

- Changed `TaskViewModel` to extend `ViewModel`.
- Updated its constructor to receive `TaskRepository`.
- Created `TaskViewModelFactory`.
- Moved database, DAO, and Repository construction into the factory.
- Updated `TaskDetailsActivity` to obtain the ViewModel through the factory using the existing `by viewModels` delegate.

The ViewModel no longer directly accesses `AppDatabase`, `TaskDao`, Room, or Android `Application`.

### Verification

- ViewModel boundary inspection: Passed.
- Activity boundary inspection: Passed.
- Gradle Build: Successful.
- Unit Tests: 1 passed, 0 skipped, 0 failed.
- Git diff check: No whitespace or formatting errors reported.

---

## 3. Current Debugging Status

The two implementation issues documented above have been resolved.

The last reported verification results for these corrections were:

- Gradle Build: Successful.
- Unit Tests: Successful.
- No unresolved implementation failure reported.

Future issues should be added below with their date, symptoms, cause, resolution, and actual verification results.
