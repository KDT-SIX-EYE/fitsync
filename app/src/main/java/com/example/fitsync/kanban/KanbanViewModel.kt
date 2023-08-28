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

//버튼 클릭과 이동을 firebase와 연동 드디어 성공!
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
                Task(id, data["title"] as String, data["description"] as String, data["status"] as String)
            } else {
                null
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            val taskRef = firestore.collection("tasks").document(taskId)

            try {
                val updateData = mapOf(
                    "status" to newStatus
                )
                taskRef.update(updateData).await()

                val fetchedTasks = fetchTasks()
                _tasks.value = fetchedTasks
            } catch (e: Exception) {
                println("Error updating status of task with ID: $taskId")
            }
        }
    }

    fun addTask(newTask: Task) {
        viewModelScope.launch {
            firestore.collection("tasks").add(newTask).await()
            val updatedTasks = _tasks.value + newTask
            _tasks.value = updatedTasks
        }
    }


    private fun loadTasks() {
        viewModelScope.launch {
            val fetchedTasks = fetchTasks()
            _tasks.value = fetchedTasks
        }
    }
}

