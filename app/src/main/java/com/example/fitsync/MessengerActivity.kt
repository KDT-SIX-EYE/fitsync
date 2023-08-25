@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitsync

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitsync.Message.Message
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider


class MessengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            val messages = remember { mutableStateListOf<Message>() }
            // ViewModelProvider 클래스의 인스턴스를 가져오기
            val viewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)

            FitSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MessengerScreen()
                }
            }

            // Firestore에서 메시지 가져오기
            db.collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) return@addSnapshotListener

                    val messageList = mutableListOf<Message>()
                    querySnapshot?.documents?.forEach { document ->
                        val message = document.toObject(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messages.clear()
                    messages.addAll(messageList)
                }
        }
    }
}

@Composable
fun MessengerScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("대화 상대 이름") },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로 가기 동작 */ }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로 가기")
                    }
                },
                actions = {
                    IconButton(onClick = { /* 검색 동작 */ }) {
                        Icon(imageVector = Icons.Filled.Search,
                            contentDescription = "검색")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "이미지 불러오기",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                RoundedTextField(modifier = Modifier.weight(1f))
            }
        },
        content = {
            ConversationContent(it)
        }
    )
}

@Composable
fun RoundedTextField(modifier: Modifier) {
    BasicTextField(
        value = "",
        onValueChange = {},
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large)
    )
}

@Composable
fun ConversationContent(paddingValues: PaddingValues) {
    // 여기서 대화 내용을 보여줄 부분을 구현하세요.
    // 대화 말풍선을 조건에 따라 생성하고 배치하는 코드를 작성합니다.
}