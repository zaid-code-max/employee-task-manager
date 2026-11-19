package com.example.employeetaskmanager

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class TaskDetailsActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(applicationContext)
    }
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        val editTextTaskTitle = findViewById<EditText>(R.id.editTextTaskTitle)
        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        val recyclerViewTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val textViewEmptyState = findViewById<View>(R.id.textViewEmptyState)

        taskAdapter = TaskAdapter { task, isCompleted ->
            viewModel.updateTaskCompletion(task.id, isCompleted)
        }

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        buttonAddTask.setOnClickListener {
            val title = editTextTaskTitle.text.toString()
            if (title.isBlank()) {
                Toast.makeText(this, R.string.error_empty_title, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertTask(title)
                editTextTaskTitle.setText("")
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTasks.collect { tasks ->
                    taskAdapter.submitList(tasks)
                    if (tasks.isEmpty()) {
                        recyclerViewTasks.visibility = View.GONE
                        textViewEmptyState.visibility = View.VISIBLE
                    } else {
                        recyclerViewTasks.visibility = View.VISIBLE
                        textViewEmptyState.visibility = View.GONE
                    }
                }
            }
        }
    }
}
