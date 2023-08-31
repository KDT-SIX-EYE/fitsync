package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity2 : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        setContent {
            UserInformationScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInformationScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    Column {
        TextField(
            value = name,
            onValueChange = { newName -> name = newName },
            label = { Text("Name") }
        )
        TextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Email") }
        )

        // Save user information to Firebase Realtime Database
        Button(onClick = { saveUserInfoToDatabase(userId, name, email) }) {
            Text("Save User Info")
        }
    }
}
fun saveUserInfoToDatabase(userId: String?, name: String, email: String) {
    if (userId != null) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userRef.child("name").setValue(name)
        userRef.child("email").setValue(email)
    }
}