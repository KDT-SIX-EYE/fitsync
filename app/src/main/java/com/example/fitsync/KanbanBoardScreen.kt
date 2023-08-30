package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import com.example.fitsync.ViewModel.KanbanViewModel
import com.example.fitsync.data.Task
import java.util.UUID

class KanbanBoardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

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
        ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(
                    onClick = showDialog,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                if (isAddTaskDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { isAddTaskDialogVisible = false },
                        title = { Text("할 일 추가", color = Color.Black) },
                        confirmButton = { /* 비어있음 */ },
                        dismissButton = {
                            Button(
                                onClick = { isAddTaskDialogVisible = false },
                                colors = ButtonDefaults.buttonColors(Color.Black),
                                modifier = Modifier
                                    .height(50.dp)
                                    .padding(bottom = 8.dp)
                            ) {
                                Text("닫기", color = Color.White)
                            }
                        },
                        text = {
                            AddTaskForm(viewModel = viewModel)
                        },
                    )
                }
            }
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("할 일 (ex : 워크인 상담)", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("설명 (ex : 담당자/pm 2~4)", color = Color.Gray) },
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
            colors = ButtonDefaults.buttonColors(Color.Black),
            modifier = Modifier
                .align(Alignment.End)

        ) {
            Text("할 일 추가", color = Color.White)
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
            .background(Color.Transparent)
    ) {
        Text(
            text = status,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            tasks.forEach { task ->
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
    var isEditDialogVisible by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var status by remember { mutableStateOf(task.status) }




    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { isEditDialogVisible = true },
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp // 여기에서 폰트 크기를 조절합니다.
                )
                Text(
                    text = description,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val newStatus = when (status) {
                        "To Do" -> "In Progress"
                        "In Progress" -> "Done"
                        "Done" -> {
                            onTaskDeleted(task)
                            return@Button
                        }
                        else -> status
                    }

                    viewModel.updateTaskStatusInViewModel(task.id, newStatus)
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = when (status) {
                        "To Do" -> "시작"
                        "In Progress" -> "완료"
                        "Done" -> "삭제"
                        else -> ""
                    },
                    color = Color.White
                )
            }
        }


        if (isEditDialogVisible) {
            AlertDialog(
                onDismissRequest = { isEditDialogVisible = false },
                title = { Text("작업 수정", color = Color.Black) },
                confirmButton = {
                    Button(
                        onClick = {
                            val updatedTask = task.copy(
                                title = title,
                                description = description,
                                status = status
                            )
                            viewModel.updateTask(task.id, updatedTask)

                            isEditDialogVisible = false
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Text("수정", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isEditDialogVisible = false },
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Text("취소", color = Color.White)
                    }
                },
                text = {
                    Column {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("할 일 (ex : 워크인 상담)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("설명 (ex : 담당자/pm 2~4)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            )
        }
    }
}