package com.example.fitsync.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.fitsync.kanban.data.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardScreen(viewModel: KanbanViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    var isAddTaskDialogVisible by remember { mutableStateOf(false) }
    val showDialog = { isAddTaskDialogVisible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ){
            StatusColumn(
                status = "해야 할 일",
                tasks = tasks.filter { it.status == "To Do" },
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatusColumn(
                status = "진행 중",
                tasks = tasks.filter { it.status == "In Progress" },
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatusColumn(
                status = "완료",
                tasks = tasks.filter { it.status == "Done" },
                viewModel = viewModel
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // 카드 아래에 공간 확보

        // 아이콘 버튼을 화면 우측하단에 고정
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .wrapContentSize(align = Alignment.BottomEnd)
        ) {
            IconButton(
                onClick = showDialog,
                modifier = Modifier
                    .size(56.dp) // 아이콘 버튼 크기 조정
                    .background(Color.Black, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }


        if (isAddTaskDialogVisible) {
            AlertDialog(
                onDismissRequest = { isAddTaskDialogVisible = false },
                title = { Text("할 일 추가") },
                confirmButton = { /* 비어있음 */ },
                dismissButton = {
                    Button(
                        onClick = { isAddTaskDialogVisible = false }
                    ) {
                        Text("닫기")
                    }
                },
                text = {
                    AddTaskForm(viewModel = viewModel)
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskForm(viewModel: KanbanViewModel) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("할 일") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("설명") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Button(
            onClick = {
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    title = title.text,
                    description = description.text,
                    status = "To Do"
                )
                viewModel.addTask(newTask)
                title = TextFieldValue("")
                description = TextFieldValue("")
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
        ) {
            Text("할 일 추가")
        }
    }
}

@Composable
fun StatusColumn(
    status: String,
    tasks: List<Task>,
    viewModel: KanbanViewModel
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .padding(8.dp)
            .background(androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.1f))
    ) {
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    viewModel = viewModel,
                    onTaskDeleted = { deletedTask ->
                        viewModel.deleteTask(deletedTask.id)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    viewModel: KanbanViewModel,
    onTaskDeleted: (Task) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val buttonClicked = remember { mutableStateOf(false) }
    val isEditDialogVisible = remember { mutableStateOf(false) }
    val editedTask = remember { mutableStateOf(task.copy()) } // 복사본 생성

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { /* Handle card click if needed */ }
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = editedTask.value.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = editedTask.value.description,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        isEditDialogVisible.value = true
                    }
                ) {
                    Text("수정")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (!buttonClicked.value) {
                            buttonClicked.value = true
                            val newStatus = when (task.status) {
                                "To Do" -> "In Progress"
                                "In Progress" -> "Done"
                                "Done" -> {
                                    onTaskDeleted(task)
                                    return@Button
                                }

                                else -> task.status
                            }

                            coroutineScope.launch {
                                viewModel.updateTaskStatus(editedTask.value.id, newStatus)
                                delay(500)
                                buttonClicked.value = false
                            }
                        }
                    },
                    enabled = !buttonClicked.value
                ) {
                    Text(
                        text = when (task.status) {
                            "To Do" -> "Start"
                            "In Progress" -> "Complete"
                            "Done" -> "Delete"
                            else -> ""
                        }
                    )
                }
            }
        }
    }

    if (isEditDialogVisible.value) {
        AlertDialog(
            onDismissRequest = {
                isEditDialogVisible.value = false
                editedTask.value = task.copy() // 수정 취소 시 복사본으로 초기화
            },
            title = { Text("할 일 수정") },
            confirmButton = {
                Button(
                    onClick = {
                        isEditDialogVisible.value = false
                        val updatedTask = task.copy(
                            title = editedTask.value.title,
                            description = editedTask.value.description
                        )
                        // 백그라운드에서 코루틴으로 호출
                        viewModel.viewModelScope.launch {
                            viewModel.updateTaskInFirestore(updatedTask)
                        }
                    }
                ) {
                    Text("저장")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isEditDialogVisible.value = false
                        editedTask.value = task.copy() // 수정 취소 시 복사본으로 초기화
                    }
                ) {
                    Text("취소")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = editedTask.value.title,
                        onValueChange = { editedTask.value = editedTask.value.copy(title = it) },
                        label = { Text("할 일") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    TextField(
                        value = editedTask.value.description,
                        onValueChange = {
                            editedTask.value = editedTask.value.copy(description = it)
                        },
                        label = { Text("설명") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}