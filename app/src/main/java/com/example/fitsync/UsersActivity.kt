package com.example.fitsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitsync.data.Firestorerole
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UsersActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        val trainer = db.collection("trainer")
        val manager = db.collection("manager")
        val Arbeit = db.collection("Arbeit")

        firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            FitSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UsersScreen(listOf(trainer, manager, Arbeit))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(memberCollections: List<CollectionReference>) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fit Sync",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* 메뉴 아이콘 */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "메뉴 아이콘"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, MyProfileActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "사용자 프로필"
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
                    val context = LocalContext.current

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, CalendarActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "캘린더 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "캘린더",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "메인 액티비티(홈)으로 이동"
                            )
                        }
                        Text(
                            text = "Home",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, AttendanceActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_qr_code_2_24),
                                contentDescription = "QR 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "QR",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, UsersActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "사용자 목록 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "프로필",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, MessengerActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_mark_chat_unread_24),
                                contentDescription = "메신저 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "채팅방",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                }
            }
        }    ) { innerPadding ->

                MembersScreen(memberCollections)

    }
}

@Composable
fun MembersScreen(memberCollections: List<CollectionReference>) {
    var selectedRole by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var member by remember { mutableStateOf(emptyList<Firestorerole>()) }

    LaunchedEffect(selectedRole) {
        val firestore = Firebase.firestore

        scope.launch {
            val memberList = withContext(Dispatchers.IO) {
                val combinedMemberList = memberCollections.flatMap { collection ->
                    val querySnapshot = collection.whereEqualTo("role", selectedRole).get().await()
                    querySnapshot.documents.map { document ->
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        Firestorerole(name, email, selectedRole)
                    }
                }
                combinedMemberList
            }
            member = memberList
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DropDownMenued(selectedRole, onChangeItem = { selectedRole = it })
        LazyColumn(
            modifier = Modifier.padding(16.dp).height(500.dp)
        ) {
            items(member) { member ->
                MemberCard(member)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenued(selectedItem: String, onChangeItem: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val listed = listOf("trainer", "manager", "Arbeit")

    Column(modifier = Modifier.padding(20.dp)) {
        ExposedDropdownMenuBox(
            modifier = Modifier.padding(30.dp),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            content = {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedItem,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text(text = "Select Role") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    content = {
                        listed.forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = role)
                                },
                                onClick = {
                                    onChangeItem(role)
                                    expanded = false
                                },
                            )
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun MemberCard(user: Firestorerole) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Name: ${user.name}")
            Text(text = "Email: ${user.email}")
            Text(text = "Role: ${user.role}")
        }
    }
}
