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
