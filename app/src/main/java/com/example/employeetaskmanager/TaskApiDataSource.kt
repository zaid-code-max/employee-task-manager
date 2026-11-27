package com.example.employeetaskmanager

interface TaskApiDataSource {
    suspend fun fetchTasks(): Result<List<ApiTaskDto>>
}
