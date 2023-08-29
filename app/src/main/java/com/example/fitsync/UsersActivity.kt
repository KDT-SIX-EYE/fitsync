package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
                    MembersScreen(listOf(trainer, manager, Arbeit))
                }
            }
        }
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
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.padding(16.dp)
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
