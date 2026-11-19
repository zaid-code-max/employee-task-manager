package com.example.employeetaskmanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val isCompleted: Boolean = false,
    @ColumnInfo(defaultValue = "'Medium'")
    val priority: String = "Medium"
)
