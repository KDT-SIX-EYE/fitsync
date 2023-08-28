package com.example.fitsync.kanban.data

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = ""
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "status" to status
        )
    }
}

