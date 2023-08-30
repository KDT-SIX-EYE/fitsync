package com.example.fitsync

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MemberRegistration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            SaveToFirestore(db)

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveToFirestore(db: FirebaseFirestore) {
    var textValue by remember { mutableStateOf("") }
    var numberValue by remember { mutableStateOf("") }
    var manager by remember { mutableStateOf("") }
    var trainer by remember { mutableStateOf("") }
    var etc by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = { Text("Name") },
            placeholder = { Text("Enter Your Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        TextField(
            value = numberValue,
            onValueChange = { numberValue = it },
            label = { Text("Number") },
            placeholder = { Text("Enter Your Number") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        TextField(
            value = manager,
            onValueChange = { manager = it },
            label = { Text("Manager") },
            placeholder = { Text("") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        TextField(
            value = trainer,
            onValueChange = { trainer = it },
            label = { Text("Trainer") },
            placeholder = { Text("") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )
        TextField(
            value = etc,
            onValueChange = { etc = it },
            label = { Text("Etc") },
            placeholder = { Text("") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )

        Button(
            onClick = {
                val userData = hashMapOf(
                    "name" to textValue,
                    "number" to numberValue,
                    "manager" to manager,
                    "trainer" to trainer,
                    "etc" to etc
                )

                db.collection("users").add(userData)
                //db.collection("users") 여기서 "" 안의 내용 변경해서 저장시 해당 내용의 firebase 컬렉션이 생성됨
                //db.collection("users").add(userData)

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Save")
        }

    }
}