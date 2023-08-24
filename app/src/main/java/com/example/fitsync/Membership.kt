    package com.example.fitsync

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
    import androidx.compose.ui.unit.dp
    import com.google.firebase.firestore.ktx.firestore
    import com.google.firebase.ktx.Firebase

    class Membership : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_membership)
            setContent {
                val db = Firebase.firestore
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun save() {
        val scrollState = rememberScrollState()
        var textValue by remember { mutableStateOf("") }
        var numberValue by remember { mutableStateOf("") }
        var manager by remember { mutableStateOf("") }
        var trainer by remember { mutableStateOf("") }
        var etc by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TextField(
                value = textValue,
                onValueChange = {},
                label = { Text("Name") },
                placeholder = { Text("Enter Your Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
            TextField(
                value = numberValue,
                onValueChange = {},
                label = { Text("Number") },
                placeholder = { Text("Enter Your Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
            TextField(
                value = manager,
                onValueChange = {},
                label = { Text("manager") },
                placeholder = { Text("") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
            TextField(
                value = trainer,
                onValueChange = {},
                label = { Text("trainer") },
                placeholder = { Text("") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
            TextField(
                value = etc,
                onValueChange = {},
                label = { Text("etc") },
                placeholder = { Text("") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
        }
    }

