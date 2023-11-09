@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitsync

import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.fitsync.navi.ScreenRoute
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitsync.navi.Calender
import com.example.fitsync.navi.Main_Kanban
import com.example.fitsync.navi.Login
import com.example.fitsync.navi.MemberProfile
import com.example.fitsync.navi.Membership
import com.example.fitsync.navi.Messenger
import com.example.fitsync.navi.MyProfile
import com.example.fitsync.navi.QR
import com.example.fitsync.navi.ScheduleCheck
import com.example.fitsync.navi.ScheduleManagement
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.fitsync.navi.Attendance


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //    private lateinit var mAuth: FirebaseAuth
//    private val viewModel: KanbanViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            FitSyncTheme {
//                 A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    val navController = rememberNavController()
                    val startDestination = remember {
                        ScreenRoute.Login.route
                    }
                    NavHost(navController = navController, startDestination = startDestination) {
                        extracted(navController)
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.extracted(navController: NavHostController) {
    composable(ScreenRoute.Calender.route) {
        Calender(navController)
    }
    composable(ScreenRoute.Messenger.route) {
        Messenger(navController)
    }
    composable(ScreenRoute.Login.route) {
        Login(navController)
    }
    composable(ScreenRoute.Membership.route) {
        Membership(navController)
    }
    composable(ScreenRoute.MemberProfile.route) {
        MemberProfile(navController)
    }
    composable(ScreenRoute.ScheduleCheck.route) {
        ScheduleCheck(navController)
    }
    composable(ScreenRoute.MyProfile.route) {
        MyProfile(navController)
    }
    composable(ScreenRoute.ScheduleManagement.route) {
        ScheduleManagement(navController)
    }
    composable(ScreenRoute.Main_Kanban.route) {
        Main_Kanban(navController)
    }
    composable(ScreenRoute.QR.route) {
        QR(navController)
    }
    composable(ScreenRoute.Attendance.route) {
        Attendance(navController)
    }
}


