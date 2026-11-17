# Employee Task Manager — Architecture

## Architecture Flow

Screen -> ViewModel -> Repository -> Storage

## 1. Screen

The Screen is responsible for:
- Displaying the task list.
- Accepting task input from the employee.
- Providing an action to add a task.
- Providing an action to mark a task as complete.
- Displaying the current task status.
- Showing the appropriate empty-task state.

The Screen should not directly manage task storage.

## 2. ViewModel

The ViewModel is responsible for:
- Managing task-related UI state.
- Receiving task actions from the Screen.
- Requesting task operations through the Repository.
- Exposing the current task state to the Screen.

The ViewModel should not directly access the Storage layer.

## 3. Repository

The Repository is responsible for:
- Providing a single access point for task data operations.
- Coordinating communication between the ViewModel and Storage.
- Providing task data to the ViewModel.
- Sending task creation and completion operations to Storage.

The Screen should not directly communicate with the Repository.

## 4. Storage

The Storage layer is responsible for:
- Storing task data.
- Retrieving task data.
- Updating task completion state.
- Providing persistent task data to the Repository.

The Storage implementation will be selected during implementation planning.

## 5. Data Flow

### View Tasks

Screen -> ViewModel -> Repository -> Storage

Storage -> Repository -> ViewModel -> Screen

### Add Task

Screen -> ViewModel -> Repository -> Storage

### Mark Task Complete

Screen -> ViewModel -> Repository -> Storage

Storage -> Repository -> ViewModel -> Screen

### View Task Status

Storage -> Repository -> ViewModel -> Screen

## 6. Existing Application Structure

The existing application contains:
- MainActivity
- TaskDetailsActivity
- Home screen layout
- Task Details screen layout

Existing Home -> Task Details navigation should be preserved unless the approved implementation requires a change.

## 7. Architecture Constraints

- The Screen must not directly access Storage.
- The ViewModel must not directly access Storage.
- The Repository acts as the data-access boundary for the ViewModel.
- Task state should have a consistent source of truth.
- Existing working functionality should not be removed unnecessarily.
- Implementation should follow the approved architecture before code changes begin.
