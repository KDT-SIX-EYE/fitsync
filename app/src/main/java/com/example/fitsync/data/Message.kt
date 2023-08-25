package com.example.fitsync.Message

// 데이터 모델 클래스 생성 : 메시지 데이터를 표현하기 위한 데이터 클래스를 생성
data class Message(
    val text: String? = null,
    val name: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val photoUrl: String? = null,
    val imageUrl: String? = null
)
