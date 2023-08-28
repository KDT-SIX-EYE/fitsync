package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MemberProfile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        val users = db.collection("users")

        setContent {
            UsersScreen()
        }
    }
}

data class FirestoreUser(
    //id == uid
    val id: String,
    val name: String = "",
    val number: String = "",
    val manager: String = "",
    val trainer: String = "",
    val etc: String = ""
)

@Composable
fun UsersScreen() {
    var users by remember { mutableStateOf(emptyList<FirestoreUser>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()

        scope.launch {
            val usersList = withContext(Dispatchers.IO) {
                //"users" firestore 콘솔 컬렉션 이름
                val querySnapshot = firestore.collection("users").get().await()
                querySnapshot.documents.map { document ->
                    //id == uid
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val number = document.getString("number") ?: ""
                    val manager = document.getString("manager") ?: ""
                    val trainer = document.getString("trainer") ?: ""
                    val etc = document.getString("etc") ?: ""
                    FirestoreUser(id, name, number, manager, trainer, etc)
                }
            }
            users = usersList
        }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(users) { user ->
            UserCard(user)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun UserCard(user: FirestoreUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "ID: ${user.id}")
            Text(text = "Name: ${user.name}")
            Text(text = "Number: ${user.number}")
            Text(text = "Manager: ${user.manager}")
            Text(text = "Trainer: ${user.trainer}")
            Text(text = "Etc: ${user.etc}")
        }
    }
}