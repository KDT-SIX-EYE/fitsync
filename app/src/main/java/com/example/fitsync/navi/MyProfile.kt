package com.example.fitsync.navi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitsync.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun MyProfile(navController: NavController) {
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    MyProfileScreen(navController, firebaseAuth)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(navController: NavController, firebaseAuth: FirebaseAuth) {
    val currentUser: FirebaseUser? = firebaseAuth.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ScreenRoute.Main.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
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
                            navController.navigate(ScreenRoute.Calender.route)
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
                            navController.navigate(ScreenRoute.QR.route)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_schedule_24),
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
                            navController.navigate(ScreenRoute.Main.route)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_call_24),
                                contentDescription = "메인 액티비티로 이동"
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
//                            val intent = Intent(context, UsersActivity::class.java)
//                            context.startActivity(intent)
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
                            navController.navigate(ScreenRoute.Messenger.route)
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ProfileComponent(userName = currentUser?.displayName ?: "Unknown")
            }
        }
    }
}

@Composable
fun ProfileComponent(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 사용자 이름
        Text(
            text = userName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 사용자 소개
        Text(
            text = "Fitness enthusiast | Yoga lover | Runner",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
