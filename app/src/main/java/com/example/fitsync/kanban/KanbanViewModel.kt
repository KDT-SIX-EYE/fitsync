package com.example.fitsync.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitsync.kanban.data.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Delete시 firebase에서 삭제
class KanbanViewModel : ViewModel() {
    private val firestore = Firebase.firestore

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private suspend fun fetchTasks(): List<Task> {
        val tasksSnapshot = firestore.collection("tasks").get().await()
        return tasksSnapshot.documents.mapNotNull { doc ->
            val data = doc.data
            val id = doc.id
            if (data != null) {
                Task(
                    id,
                    data["title"] as String,
                    data["description"] as String,
                    data["status"] as String
                )
            } else {
                null
            }
        }
    }

    suspend fun updateTaskStatus(taskId: String, newStatus: String) {
        val taskRef = firestore.collection("tasks").document(taskId)

        try {
            val updateData = mapOf(
                "status" to newStatus
            )
            // 데이터베이스 업데이트
            taskRef.update(updateData).await()
        } catch (e: Exception) {
            println("Error updating status of task with ID: $taskId")
        }
    }

    suspend fun updateTaskInFirestore(editedTask: Task) {
        val taskRef = firestore.collection("tasks").document(editedTask.id)

        try {
            val updateData = mapOf(
                "title" to editedTask.title,
                "description" to editedTask.description,
                "status" to editedTask.status
            )
            taskRef.update(updateData).await()
        } catch (e: Exception) {
            println("Error updating task with ID: ${editedTask.id}")
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val taskRef = firestore.collection("tasks").document(taskId)

            try {
                taskRef.delete().await()

                val fetchedTasks = fetchTasks()
                _tasks.value = fetchedTasks
            } catch (e: Exception) {
                println("Error deleting task with ID: $taskId")
            }
        }
    }


    fun addTask(newTask: Task) {
        viewModelScope.launch {
            firestore.collection("tasks").add(newTask).await()

            // 데이터가 추가된 후에 즉시 상태 업데이트
            _tasks.value = _tasks.value + newTask
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val fetchedTasks = fetchTasks()
            _tasks.value = fetchedTasks
        }
    }
}
