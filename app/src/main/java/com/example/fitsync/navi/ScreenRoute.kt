package com.example.fitsync.navi

sealed class ScreenRoute(val route : String) {
    object Calender : ScreenRoute("calender")
    object Main_Kanban : ScreenRoute("main_kanban")
    object Main : ScreenRoute("main")
    object Messenger : ScreenRoute("messenger")
    object Login : ScreenRoute("login")
    object MemberProfile : ScreenRoute("memberprofile")
    object ScheduleCheck : ScreenRoute("schedulecheck")
    object ScheduleManagement : ScreenRoute("scheduleManagement")
    object MyProfile : ScreenRoute("myprofile")
    object Membership : ScreenRoute("membership")
    object QR : ScreenRoute("qr")
    object Attendance : ScreenRoute("attendance")
}
