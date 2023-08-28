//package com.example.fitsync
//
//import com.google.firebase.firestore.Exclude
//import com.google.firebase.firestore.IgnoreExtraProperties
//
//@IgnoreExtraProperties
//data class EventData(
//    val eventDate: String = "",
//    val eventName: String = "",
//    val registrant: String = "",
//    val startTime: Int = 0,
//    val endTime: Int = 0
//){
//    @Exclude
//    fun toMap(): Map<String, Any?> {
//        return mapOf(
//            "eventDate" to eventDate,
//            "eventName" to eventName,
//            "registrant" to registrant,
//            "startTime" to startTime,
//            "endTime" to endTime,
//        )
//    }
//}
//
//
//
//
//
