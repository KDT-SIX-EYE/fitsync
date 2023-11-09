package com.example.fitsync.navi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.example.fitsync.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(navController: NavController) {
    val loginViewModel = LoginViewModel()
    LoginScreen(navController, viewModel = loginViewModel) { success ->
        if (success) {
            navController.navigate(ScreenRoute.Main_Kanban.route)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel, onLoginSuccess: (Boolean) -> Unit) {
    val context = LocalContext.current

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val emailState = remember { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    val passwordState = remember { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }

    val rememberCredentialsState = remember { mutableStateOf(sharedPreferences.getBoolean("rememberCredentials", true)) }

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

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Email, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    val email = emailState.value
                    val password = passwordState.value

                    viewModel.login(email, password)

                    if (rememberCredentialsState.value) {
                        // 이메일과 비밀번호를 SharedPreferences에 저장
                        with(sharedPreferences.edit()) {
                            email?.let { putString("email", it) }
                            password?.let { putString("password", it) }
                            putBoolean("rememberCredentials", rememberCredentialsState.value)

                            apply()
                        }
                    } else {
                        // CheckBox가 선택되지 않았을 경우 저장된 데이터를 제거
                        with(sharedPreferences.edit()) {
                            remove("email")
                            remove("password")
                            apply()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign In")
            }

            Button(
                onClick = {
                    navController.navigate(ScreenRoute.Membership.route)
                },
                colors = ButtonDefaults.buttonColors(
                    Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign Up")
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Checkbox(
                checked = rememberCredentialsState.value,
                onCheckedChange = {
                    rememberCredentialsState.value = it
                },
                colors = CheckboxDefaults.colors(Color.Black)
            )
            Text(text = "Remember Email and Password")
            Button(
                onClick = {
                    val email = emailState.value
                    if (email.isNotEmpty()) {
                        viewModel.sendPasswordResetEmail(email)
                    } else {
                        viewModel.errorMessage = "이메일을 입력해주세요."
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    Color.Transparent,
                    contentColor = Color.Gray
                )
            ) {
                Text(
                    text = "Forgot Password",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (viewModel.loginSuccess) {
            onLoginSuccess(true)
        }
    }
}


class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var errorMessage by mutableStateOf("")
    var loginSuccess by mutableStateOf(false)

    fun login(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginSuccess = true
                    } else {
                        errorMessage = "로그인에 실패했습니다. 다시 시도해주세요."
                    }
                }
        } else {
            errorMessage = "이메일과 비밀번호를 입력해주세요."
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isNotBlank()) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        errorMessage = "비밀번호 재설정 이메일을 보냈습니다. 이메일을 확인해주세요."
                    } else {
                        errorMessage = "비밀번호 재설정 이메일 전송에 실패했습니다. \n다시 시도해주세요."
                    }
                }
        } else {
            errorMessage = "이메일을 입력해주세요."
        }
    }
}
