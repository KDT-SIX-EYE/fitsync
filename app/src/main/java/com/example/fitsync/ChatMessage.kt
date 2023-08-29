package com.example.fitsync.data



// 데이터 모델 클래스 생성 : 메시지 데이터를 표현하기 위한 데이터 클래스를 생성
data class ChatMessage(
    val message: String? = "메시지 오류",
    val userId: String? = "UID 오류",
    val userName: String? = "이름 오류",
    val uploadDate: String? = ""
)

data class User(
    val userId: String,
    val userName: String
)

data class FirestoreUser(
    val name: String = "",
    val number: String = "",
    val manager: String = "",
    val trainer: String = "",
    val etc: String = ""
)

data class Firestorerole(
    val name: String = "",
    val email: String = "",
    val role: String = ""
)

data class Task(
    val id: String = "",
    var title: String = "",
    var description: String = "",
    var status: String = "",
)

