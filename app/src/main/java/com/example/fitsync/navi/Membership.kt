package com.example.fitsync.navi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitsync.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun Membership(navController: NavController) {
    SignupScreen(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var signupErrorMessage by remember { mutableStateOf("") }
    val auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 76.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.fitsync),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
        )
        var selectedRole by remember { mutableStateOf("") }
        DropDownMenu(selectedRole) { role ->
            selectedRole = role
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름을 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일을 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호를 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && selectedRole.isNotBlank()) {
                    val user = FirebaseAuth.getInstance().currentUser
                    // auth 저장 수정
                    val auth: FirebaseAuth = Firebase.auth
                    auth.createUserWithEmailAndPassword(email, password)
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userData = hashMapOf(
                                    "name" to name,
                                    "email" to email,
                                    "role" to selectedRole
                                )
                                db.collection(selectedRole)
                                    .add(userData)
                                    .addOnSuccessListener { documentReference ->
                                        navController.navigate(ScreenRoute.Main.route)
                                    }
                                    .addOnFailureListener { e ->
                                        signupErrorMessage = "회원가입에 실패했습니다\n이메일과 비밀번호를 확인하십시오"
                                    }
                            } else {
                                signupErrorMessage = "회원가입에 실패했습니다\n이메일과 비밀번호를 확인하십시오"
                            }
                        }
                } else {
                    signupErrorMessage = "이름, 이메일, 비밀번호, 근로 종류를 모두 입력해주세요."
                }
                navController.navigate(ScreenRoute.Main.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("회원가입")
        }

        if (signupErrorMessage.isNotEmpty()) {
            Text(
                text = signupErrorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(selectedItem: String, onChangeItem: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val list = listOf("trainer", "manager", "Arbeit")

    Column(modifier = Modifier
        .padding(20.dp)
        .fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedItem,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(text = "selct your work type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEach { label ->
                    DropdownMenuItem(
                        text = {
                            Text(text = label)
                        },
                        onClick = {
                            onChangeItem(label)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}