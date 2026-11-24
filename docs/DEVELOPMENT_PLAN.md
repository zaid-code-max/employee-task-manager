# Employee Task Manager — Development Plan

## Historical Status of Sections 1–6

Sections 1–6 preserve the original implementation plan written before the current application baseline was completed. They are retained as project history and are not outstanding implementation tasks.
The current verified application already contains the ViewModel, Repository, Room Storage layer, and existing Home -> Task Details navigation. The Day 6 API retrieval plan is documented separately below.

## 1. Architecture (Original Plan - Historical)

Follow the planned application flow:

Screen -> ViewModel -> Repository -> Storage

The UI layer will handle user interaction and display.
The ViewModel will manage screen state and coordinate task operations.
The Repository will provide a single access point for task data.
The Storage layer will provide task persistence.

## 2. Existing Files to Preserve

- app/src/main/java/com/example/employeetaskmanager/MainActivity.kt
- app/src/main/java/com/example/employeetaskmanager/TaskDetailsActivity.kt
- app/src/main/res/layout/layout.xml
- app/src/main/res/layout/activity_task_details.xml
- app/src/main/AndroidManifest.xml

Existing Home -> Task Details navigation should be preserved unless the planned design requires a change.

## 3. Planned Files and Layers (Original Plan - Historical)

The original plan intended to introduce the ViewModel, Repository, and Storage layers. These layers are already present in the current verified application baseline; this sentence records the earlier plan, not pending work.

Original planned responsibilities (historical reference):

### Screen
- Display tasks.
- Provide task creation controls.
- Allow task completion.
- Display task status.

### ViewModel
- Manage task-related UI state.
- Receive task actions from the Screen.
- Request task operations through the Repository.

### Repository
- Provide a single interface for task data operations.
- Coordinate data access between the ViewModel and Storage.

### Storage
- Store task data.
- Retrieve task data.
- Update task completion state.

## 4. Original Implementation Order (Historical)

The numbered sequence below records the original implementation order. It is retained for project history and is not a current outstanding-task list. The ViewModel, Repository and Storage layers already exist in the current baseline.

1. Define the task data requirements.
2. Design the Storage layer.
3. Create the Repository layer.
4. Create the ViewModel layer.
5. Update the required Screen UI.
6. Connect Screen -> ViewModel -> Repository -> Storage.
7. Implement task viewing.
8. Implement task creation.
9. Implement task completion.
10. Implement task status display.
11. Handle the defined edge cases.
12. Run tests and verify acceptance criteria.

## 5. Testing Plan

### Task Viewing
- Verify available tasks can be displayed.
- Verify the empty task state is handled.

### Task Creation
- Verify a valid task can be added.
- Verify empty required input is rejected.
- Verify the new task appears in the task list.

### Task Completion
- Verify a pending task can be marked complete.
- Verify an already completed task is handled safely.

### Task Status
- Verify pending status is displayed correctly.
- Verify completed status is displayed correctly.
- Verify status updates after completion.

## 6. Safety and Change Control

- Do not remove working Day 1 or Day 2 functionality unnecessarily.
- Preserve existing Home -> Task Details navigation unless a planned change requires it.
- Make changes incrementally.
- Verify the project after each implementation stage.
- Do not begin implementation until the plan and architecture have been reviewed and approved.

---

## Day 6 — API-Backed Task-List Retrieval (Planning Only)

### 1. Objective
Plan mock REST API task retrieval using the existing MVVM architecture. Day 6 covers requirements, architecture, API contract planning, error states and test planning only. No implementation is included.

### 2. Verified Existing Application Baseline
The following details are based on the inspected application source:

- `AppDatabase`: Room database version 4, database name `task_database`.
- Database builder uses `fallbackToDestructiveMigration(true)`. No explicit migration registration or migration definition was found in the inspected `app/src` source.
- `TaskEntity`: `id`, `title`, `isCompleted` and `priority`.
- `TaskDao`: exposes tasks through `Flow<List<TaskEntity>>` and provides insert, completion-update and delete operations.
- `TaskRepository`: currently delegates task storage operations to the Room DAO; no API client is currently present.
- `TaskViewModel`: exposes task and filter state through StateFlows.
- `TaskDetailsActivity`: observes task data and displays the task list, filters and dashboard counts.

### 3. Proposed Retrieval Flow — Pending Approval
Candidate flow:

`TaskDetailsActivity → TaskViewModel → TaskRepository → Mock API`

A proposed DTO mapper would convert API response data into the application's agreed task model. The repository would coordinate remote retrieval and local data access.

The exact persistence and data-flow strategy, including whether API data should be saved to Room before being displayed, remains **[PENDING — TRAINER APPROVAL]**.

### 4. Proposed Loading and Error States
Plan for the following states:

- Initial loading when no cached data is available.
- Cached task list remains visible during refresh.
- Successful response containing tasks.
- Successful empty response.
- Offline state with cached data.
- Offline state without cached data, with retry.
- Server error (HTTP 5xx) with cached data.
- Server error without cached data, with retry.
- Client error (HTTP 4xx).
- Request timeout.
- Invalid or malformed response data.
- Retry after a failed request.

Exact UI behavior and error messages require trainer review.

### 5. Draft API Contract — Unverified
The following is a proposal, not a confirmed API specification:

- Method: `GET`
- Candidate endpoint: `/api/v1/tasks`
- Candidate response format: JSON array.
- Candidate fields: `id` (Long), `title` (String), `isCompleted` (Boolean), `priority` (String).
- HTTP success behavior, empty response behavior, nullability, authentication headers and error response format are not confirmed.

All contract details require **[PENDING — TRAINER APPROVAL]**.

### 6. Candidate Files — Not Created
These are possible files for a later implementation phase. They must not be created during Day 6:

- `TaskApiClient.kt` — API request configuration and retrieval.
- `ApiTaskDto.kt` — representation of the API response.
- `ApiTaskMapper.kt` — conversion from API DTO to the approved application data model.

Final file names, package locations and API library selection require approval.

### 7. Data Integrity and Compatibility Risks
- Local Room IDs and remote API IDs may conflict.
- Duplicate remote IDs and task update conflicts need an approved resolution strategy.
- An empty API response must not accidentally erase existing local records.
- Offline task creation, completion and deletion need an agreed synchronization policy.
- The existing destructive migration fallback is a local data-loss risk that must be considered separately from API integration.
- Existing Room-based behavior must remain compatible with any future retrieval implementation.

### 8. Planned Test Coverage — Not Executed
Tests to be designed and executed after approval:

- API response mapping and field validation.
- Successful response containing tasks.
- Empty JSON array and HTTP 204 responses when Room already contains records.
- Malformed JSON, missing fields and invalid priority values.
- Duplicate or conflicting remote IDs.
- Offline behavior with and without cached tasks.
- Timeout and retry behavior.
- HTTP 4xx and 5xx handling.
- Local task creation, completion and deletion while offline.
- Regression tests for existing sorting, filters, dashboard counts, delete confirmation, persistence and navigation.

### 9. Trainer Approval Gate
Before implementation, obtain decisions on:

- API endpoint and response contract.
- Authentication requirements.
- API client library.
- Room/API source of truth.
- Cache, merge or replacement policy.
- Empty-response behavior.
- Remote/local ID mapping and conflict resolution.
- Offline write synchronization.
- Error-state behavior and retry policy.

All unresolved decisions remain **[PENDING — TRAINER APPROVAL]**.

### 10. Day 6 Scope Restriction
This document records planning only. No API code, UI code, database changes, dependency changes or tests are implemented or claimed as completed. Implementation must wait for trainer approval.
