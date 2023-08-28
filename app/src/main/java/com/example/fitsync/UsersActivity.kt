package com.example.fitsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitsync.data.User
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UsersActivity : ComponentActivity() {
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
                    UserListScreen()
                }
            }
        }
    }
}

// 사용자 목록 만들기 (채팅을 위한)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen() {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = firebaseAuth.currentUser
    var userList by remember { mutableStateOf(listOf<User>()) }

    // 사용자 목록 불러오기
    loadUserList { users ->
        userList = users
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Users",
                    fontSize = 17.sp,
                    fontFamily = FontFamily.SansSerif
                )},
                navigationIcon = {
                    IconButton(onClick = {
                        // 메인 액티비티로 이동
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(userList) { user ->
                UserListItem(
                    user = user
                )
            }
        }
    }
}

@Composable
fun UserListItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.userimage), // 기본 프로필 이미지 리소스 사용
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp) // 프로필 사진 크기 조정
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = user.userName,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}

fun loadUserList(listener: (List<User>) -> Unit) {
    // 여기서 사용자 목록을 불러오는 로직을 구현하세요.
    // 예를 들어 Firebase Realtime Database나 Firestore를 사용하여 사용자 목록을 가져올 수 있습니다.
    // 가져온 사용자 목록은 listener에 전달하여 업데이트합니다.

    // 이 예시에서는 더미 데이터를 사용하여 사용자 목록을 생성합니다.
    val dummyUserList = listOf(
        User(userId = "user1", userName = "민형준"),
        User(userId = "user2", userName = "박준호"),
        User(userId = "user3", userName = "김종대"),
        User(userId = "user4", userName = "김상은"),
        User(userId = "user5", userName = "신명호"),
        User(userId = "user6", userName = "안은진"),
        User(userId = "user1", userName = "민형준"),
        User(userId = "user2", userName = "박준호"),
        User(userId = "user3", userName = "김종대"),
        User(userId = "user4", userName = "김상은"),
        User(userId = "user5", userName = "신명호"),
        User(userId = "user6", userName = "안은진"),
        User(userId = "user1", userName = "민형준"),
        User(userId = "user2", userName = "박준호"),
        User(userId = "user3", userName = "김종대"),
        User(userId = "user4", userName = "김상은"),
        User(userId = "user5", userName = "신명호"),
        User(userId = "user6", userName = "안은진")
        )
    listener(dummyUserList)
}
