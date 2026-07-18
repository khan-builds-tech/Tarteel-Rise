package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.services.AlarmStateHolder
import com.example.ui.screens.AlarmActiveScreen
import com.example.ui.screens.AlarmCreateScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.StreakSuccessScreen
import com.example.ui.theme.TarteelRiseTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TarteelRiseTheme {
                val navController = rememberNavController()
                
                // Retrieve or create our unified ViewModel
                val mainViewModel: MainViewModel = viewModel {
                    MainViewModel(applicationContext)
                }

                val isRinging by mainViewModel.isRinging.collectAsState()
                val navigationTrigger by mainViewModel.navigationTrigger.collectAsState()

                // Automated low-level routing trigger based on the global state of the Alarm Manager
                LaunchedEffect(isRinging) {
                    if (isRinging) {
                        navController.navigate("alarm_active") {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                        }
                    }
                }

                // Automated routing trigger based on successful recitation score reaching 80%
                LaunchedEffect(navigationTrigger) {
                    navigationTrigger?.let { trigger ->
                        if (trigger == "SUCCESS") {
                            navController.navigate("success") {
                                popUpTo("alarm_active") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = mainViewModel,
                                onNavigateToCreate = { navController.navigate("create") }
                            )
                        }

                        composable("create") {
                            AlarmCreateScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("alarm_active") {
                            AlarmActiveScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable("success") {
                            StreakSuccessScreen(
                                viewModel = mainViewModel,
                                onNavigateToDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("success") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // If coming back from active alarm intent, low-level routing is handled by state observer.
    }
}
