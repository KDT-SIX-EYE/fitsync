package com.example.fitsync.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.fitsync.kanban.data.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardScreen(viewModel: KanbanViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    val db = Firebase.firestore

    @Composable
    fun AddTaskForm(onTaskAdded: (Task) -> Unit) {
        var title by remember { mutableStateOf(TextFieldValue()) }
        var description by remember { mutableStateOf(TextFieldValue()) }

        Column {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Button(
                onClick = {
                    val newTask = Task(
                        id = UUID.randomUUID().toString(),
                        title = title.text,
                        description = description.text,
                        status = "To Do"
                    )
                    onTaskAdded(newTask)
                    title = TextFieldValue("")
                    description = TextFieldValue("")
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text("Add Task")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                StatusColumn(
                    status = "To Do",
                    tasks = tasks.filter { it.status == "To Do" },
                    onTaskStatusChanged = { task, newStatus ->
                        viewModel.updateTaskStatus(task.id, newStatus)
                    },
                    onTaskDeleted = { task ->
                        viewModel.deleteTask(task.id)
                    }
                )

            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                StatusColumn(
                    status = "In Progress",
                    tasks = tasks.filter { it.status == "In Progress" },
                    onTaskStatusChanged = { task, newStatus ->
                        viewModel.updateTaskStatus(task.id, newStatus)
                    },
                    onTaskDeleted = { task ->
                        viewModel.deleteTask(task.id)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            StatusColumn(
                status = "Done",
                tasks = tasks.filter { it.status == "Done" },
                onTaskStatusChanged = { task, newStatus ->
                    viewModel.updateTaskStatus(task.id, newStatus)
                },
                onTaskDeleted = { task ->
                    viewModel.deleteTask(task.id)
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            AddTaskForm { newTask ->
                viewModel.addTask(newTask)
            }
        }
    }
}

@Composable
fun StatusColumn(
    status: String,
    tasks: List<Task>,
    onTaskStatusChanged: (Task, String) -> Unit,
    onTaskDeleted: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)
    ) {
        Text(text = status, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.height(8.dp))

        tasks.forEach { task ->
            TaskCard(task, onTaskStatusChanged, onTaskDeleted)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onTaskStatusChanged: (Task, String) -> Unit,
    onTaskDeleted: (Task) -> Unit // 이 파라미터 추가
) {
    val currentStatus = remember { mutableStateOf(task.status) }
    val showCard = remember { mutableStateOf(true) } // TaskCard 표시 여부

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(androidx.compose.ui.graphics.Color.LightGray)
            .padding(8.dp)
    ) {
        if (showCard.value) {
            Text(text = task.title, fontWeight = FontWeight.Bold)
            Text(text = task.description)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val newStatus = when (task.status) {
                        "To Do" -> "In Progress"
                        "In Progress" -> "Done"
                        "Done" -> {
                            // Done 상태에서 삭제 버튼 클릭 시 TaskCard 및 데이터 삭제
                            onTaskDeleted(task)
                            showCard.value = false
                            return@Button
                        }
                        else -> task.status
                    }

                    onTaskStatusChanged(task, newStatus)
                    currentStatus.value = newStatus
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(
                    when (task.status) {
                        "To Do" -> "Start"
                        "In Progress" -> "Complete"
                        "Done" -> "Delete"
                        else -> ""
                    }
                )
            }
        }

        if (showCard.value) {
            // LaunchedEffect를 사용하여 상태가 변경되었을 때 UI 업데이트
            LaunchedEffect(currentStatus.value) {
                // 상태 변경에 따른 UI 업데이트 로직 작성 (필요하면)
            }
        }
    }
}

