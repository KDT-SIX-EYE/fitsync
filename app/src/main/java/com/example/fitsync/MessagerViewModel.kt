package com.example.fitsync

import androidx.lifecycle.ViewModel
import com.example.fitsync.Message.Message
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel을 상속받고, Firestore에서 메시지 데이터를 가져와서 messages 변수에 저장하고 갱신하는 역할
class MessengerViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        // Firestore에서 메시지 가져오기
        db.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) return@addSnapshotListener

                val messageList = mutableListOf<Message>()
                querySnapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                _messages.value = messageList
            }
    }

    // sendMessage 메서드 : FireStore을 통해 메시지를 전송하는 로직
    fun sendMessage(messageText: String, senderName: String) {
        val newMessage = Message(messageText, senderName, System.currentTimeMillis())
        db.collection("messages")
            .add(newMessage)
            .addOnSuccessListener {
                // Message sent successfully
            }
            .addOnFailureListener { e ->
                // Handle message sending failure
            }
    }
}
