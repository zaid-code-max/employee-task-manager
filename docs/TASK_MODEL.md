# Employee Task Manager — Task Data Model

## Task Entity

Each task will contain the following fields:

| Field | Type | Purpose |
|---|---|---|
| id | Long | Uniquely identifies the task |
| title | String | Stores the task details |
| isCompleted | Boolean | Stores whether the task is pending or completed |

## Task Status

The task status is derived from `isCompleted`:

- `false` -> Pending
- `true` -> Completed

## Validation & Business Rules

- **id**: Must be unique for every task to ensure persistent identity.
- **title**: Must not be empty or blank (addresses edge case EC-02: Empty Task Input).
- **isCompleted**: Defaults to `false` when a new task is created.
- **Duplicate Handling**: Tasks with identical titles or IDs should be handled appropriately according to edge case requirements (EC-03).
