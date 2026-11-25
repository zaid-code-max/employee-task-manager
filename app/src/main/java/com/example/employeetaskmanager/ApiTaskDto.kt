package com.example.employeetaskmanager

data class ApiTaskDto(
    val userId: Int,
    val id: Long,
    val title: String,
    val completed: Boolean
)
