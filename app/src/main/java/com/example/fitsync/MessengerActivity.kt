package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitsync.Message.ChatMessage
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase.getInstance
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessengerActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            FitSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(firebaseAuth = firebaseAuth)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(firebaseAuth: FirebaseAuth) {
    val user: FirebaseUser? = firebaseAuth.currentUser
    var composingMessage by remember { mutableStateOf("") }
    var sentMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val context = LocalContext.current

    // 이전 채팅 메시지 불러오기
    loadChatMessages { messages ->
        sentMessages = messages
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Messenger", fontSize = 17.sp, fontFamily = FontFamily.SansSerif) },
                navigationIcon = {
                    IconButton(onClick = { /* 채팅 목록 액티비티로 이동 */
//                        val intent = Intent(context, ChatListActivity::class.java)
//                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 채팅 내용 검색 동작 */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { /* 더보기 버튼을 누르면 뭐가 나와야 할까 */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "더보기")
                    }
                    CustomTextField(
                        value = composingMessage,
                        onValueChange = { composingMessage = it },
                        onSendClick = {
                            if (composingMessage.isNotEmpty()) {
                                val currentDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                val newChatMessage =
                                    ChatMessage(
                                        // data class에 맞춰 바꾸기 (로그인)
                                        message = composingMessage,
                                        userId = user?.uid,
                                        userName = user?.displayName,
                                        uploadDate = currentDate)
//                                sentMessages += composingMessage
                                saveChatMessage(newChatMessage)
                                composingMessage = ""
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(sentMessages.reversed()) { message ->
                ChatItemBubble(
                    message = message,
                    UserId = user?.uid
                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(end = 20.dp, top = 10.dp, bottom = 10.dp),
//                    contentAlignment = Alignment.BottomEnd
//                ) {
//
//                }
            }
        }
    }
}

fun loadChatMessages(listener: (List<ChatMessage>) -> Unit) {
    val database = getInstance("https://fit-sync-76834-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val chatRef = database.getReference("LoadChat")

    chatRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messages = mutableListOf<ChatMessage>()
            for (childSnapshot in snapshot.children) {
                val message = childSnapshot.getValue(ChatMessage::class.java)
                message?.let {
                    messages.add(it)
                }
            }
            listener(messages)
        }
        override fun onCancelled(error: DatabaseError) {
            // 에러 처리
        }
    })
}



fun saveChatMessage(chatmessage: ChatMessage) {
    val database = getInstance("https://dataclass-27aac-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val chatRef = database.getReference("SaveChat")
    val newMessageRef = chatRef.push() // 새로운 메시지를 추가하기 위한 참조

    newMessageRef.setValue(chatmessage)
}


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f) // 여기서 비율을 조정
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color.White),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        IconButton(
            onClick = onSendClick
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "메시지 전송 버튼"
            )
        }
    }
}

@Composable
fun ChatItemBubble(
    message: ChatMessage,
    UserId: String?
) {
    val isCurrentUserMessage = UserId == message.userId
    val bubbleColor = if (isCurrentUserMessage) Color.DarkGray else Color.Blue // 사용자와 상대방 메시지에 다른 색 지정
    val alignment = if (isCurrentUserMessage) Alignment.BottomEnd else Alignment.BottomStart
    Column {
        if (!isCurrentUserMessage) {
            Text(text = message.userName ?: "")
        }
        Row {
            if (isCurrentUserMessage){
                Text(text = message.uploadDate ?: "")
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)
                    .background(bubbleColor),
                contentAlignment = alignment
            ) {
                Text(
                    text = message.message ?: "",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp))
            }
            if (!isCurrentUserMessage) {
                Text(
                    text = message.uploadDate ?: "",
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val firebaseAuth = FirebaseAuth.getInstance() // Firebase 초기화 및 인스턴스 생성
    FitSyncTheme {
        ChatScreen(firebaseAuth = firebaseAuth)
    }
}