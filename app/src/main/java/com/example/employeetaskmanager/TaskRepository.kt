package com.example.employeetaskmanager

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val taskApiClient: TaskApiDataSource = TaskApiClient()
) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }

    suspend fun fetchApiTasks(): Result<List<ApiTaskDto>> {
        return taskApiClient.fetchTasks()
    }
}
