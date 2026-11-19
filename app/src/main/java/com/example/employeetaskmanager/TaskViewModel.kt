package com.example.employeetaskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun insertTask(title: String, priority: String = "Medium") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(TaskEntity(title = title.trim(), priority = priority))
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
