package com.example.fitsync.Message

// 데이터 모델 클래스 생성 : 메시지 데이터를 표현하기 위한 데이터 클래스를 생성
data class ChatMessage(
    val message: String? = "메시지 오류",
    val userId: String? = "UID 오류",
    val userName: String? = "이름 오류",
    val uploadDate: String? = ""
)
