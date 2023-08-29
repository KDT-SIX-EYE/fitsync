package com.example.fitsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private val loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(viewModel = loginViewModel) { success ->
                if (success) {
                    startMainActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: (Boolean) -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    viewModel.login(emailState.value, passwordState.value)
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
                    val intent = Intent(context, Membership::class.java)
                    context.startActivity(intent)
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
            modifier =  Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Forgot Password")
            }
        }

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp))
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