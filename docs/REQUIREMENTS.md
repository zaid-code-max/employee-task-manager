# Employee Task Manager — Requirements

## Business Request

Employees must view tasks, add tasks, mark complete, and see status.

## User Stories

### US-01 — View Tasks
As an employee, I want to view my assigned tasks so that I know what work I need to complete.

### US-02 — Add Task
As an employee, I want to add a task so that I can record new work.

### US-03 — Mark Task Complete
As an employee, I want to mark a task as complete so that I can indicate that the work is finished.

### US-04 — View Task Status
As an employee, I want to see the status of a task so that I know whether it is pending or completed.

## Edge Cases

### EC-01 — No Tasks Available
If an employee has no tasks, the application should handle the empty task state clearly.

### EC-02 — Empty Task Input
If an employee tries to add a task without required task details, the application should prevent an invalid task from being added.

### EC-03 — Duplicate Task
If an employee attempts to add the same task again, the application should handle the duplicate appropriately.

### EC-04 — Already Completed Task
If an employee attempts to mark an already completed task as complete again, the application should handle the action safely.

### EC-05 — Task Status Consistency
After a task is marked complete, its displayed status should remain consistent with its completion state.

## Acceptance Criteria

### US-01 — View Tasks
- Employee can open and view the available task list.
- Available tasks are clearly visible.
- If no tasks are available, the empty task state is handled clearly.

### US-02 — Add Task
- Employee can enter the required task details and add a task.
- An invalid task cannot be created when required input is empty.
- A successfully added task is visible in the task list.

### US-03 — Mark Task Complete
- Employee can mark a pending task as complete.
- After the complete action, the task is in the completed state.
- An already completed task is handled safely.

### US-04 — View Task Status
- Employee can see the current status of a task.
- A pending task shows the correct pending status.
- A completed task shows the correct completed status.
- The displayed status updates after a task is marked complete.

---

## Day 6 — API-Backed Task-List Retrieval (Planning Only)

### 1. Goal
Plan retrieval of a task list from a mock REST API using the existing MVVM architecture. No feature implementation is included in Day 6.

### 2. Existing Functionality to Preserve
- Room v4 local task persistence.
- Task creation with Low, Medium and High priority.
- One-way completion: completed tasks cannot be unchecked through the UI.
- SQL priority ordering: High, Medium, Low, other; pending before completed; id DESC.
- Task deletion with confirmation.
- All, Pending and Completed filters.
- Global dashboard counts: Total, Pending and Completed.
- Existing MainActivity to TaskDetailsActivity navigation.

### 3. Proposed API Retrieval User Story
As an employee, I want to retrieve tasks from a mock API so that remotely provided tasks can be displayed in the application.

### 4. Acceptance Criteria
- Initial loading without cached data: display a loading indicator.
- Cached data during refresh: keep existing Room tasks visible while a proposed API request runs.
- API success with tasks: map and display the returned tasks.
- Successful empty response: handle an empty JSON array or HTTP 204 without crashing.
- Offline with cache: retain cached tasks and show an offline notice.
- Offline without cache: show an offline-specific error state and retry action.
- HTTP 5xx with cache: retain cached tasks and show a server-error notice.
- HTTP 5xx without cache: show a server-error state and retry action.
- HTTP 4xx: distinguish client errors from server errors; exact handling remains pending.
- Timeout and parsing failures: distinguish these from connectivity loss and HTTP errors.
- Retry: a user retry action should request another fetch.

### 5. Pending Trainer Decisions
Every item below requires [PENDING — TRAINER APPROVAL]:
- Room/API source of truth.
- Room/API merge or replacement strategy.
- Empty API response when Room already contains records.
- Remote/local ID mapping.
- Duplicate ID and conflict resolution.
- Handling of local offline task creation, completion and deletion.
- HTTP 204 versus empty JSON array behavior.
- Authentication requirement.
- Endpoint and response contract.
- API client library selection.

### 6. Existing Baseline Risk
The inspected AppDatabase uses Room database version 4 and `fallbackToDestructiveMigration(true)`. No explicit migration registrations or definitions were found in the inspected `app/src` source. An unsupported migration path may recreate the database and cause local task data loss. This is an existing baseline risk, not an API integration feature.

### 7. Scope Constraint
Day 6 is planning only. No feature code, dependency changes or API implementation today. Trainer approval and test execution are not claimed.
