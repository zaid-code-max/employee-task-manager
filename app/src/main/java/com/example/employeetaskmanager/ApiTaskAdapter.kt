package com.example.employeetaskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ApiTaskAdapter : ListAdapter<ApiTaskDto, ApiTaskAdapter.ApiTaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiTaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_api_task, parent, false)

        return ApiTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApiTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ApiTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView =
            itemView.findViewById(R.id.textViewApiTaskTitle)

        private val statusTextView: TextView =
            itemView.findViewById(R.id.textViewApiTaskStatus)

        private val metaTextView: TextView =
            itemView.findViewById(R.id.textViewApiTaskMeta)

        fun bind(task: ApiTaskDto) {
            titleTextView.text = task.title

            statusTextView.text = if (task.completed) "COMPLETED" else "PENDING"
            statusTextView.setTextColor(
                itemView.context.getColor(
                    if (task.completed) R.color.status_green else R.color.status_amber
                )
            )

            metaTextView.text = "API ID #${task.id}  •  User ${task.userId}"
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ApiTaskDto>() {
            override fun areItemsTheSame(
                oldItem: ApiTaskDto,
                newItem: ApiTaskDto
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ApiTaskDto,
                newItem: ApiTaskDto
            ): Boolean = oldItem == newItem
        }
    }
}
