package com.example.aventuradosidiomas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aventuradosidiomas.data.AppDatabase
import com.example.aventuradosidiomas.data.repository.GameRepository
import com.example.aventuradosidiomas.data.util.DataPreloader
import com.example.aventuradosidiomas.ui.screens.*
import com.example.aventuradosidiomas.ui.theme.AventuraDosIdiomasTheme
import com.example.aventuradosidiomas.ui.viewmodel.GameViewModel
import com.example.aventuradosidiomas.ui.viewmodel.GameViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val repository = GameRepository(database)

        // Pré-carregar dados de exemplo
        lifecycleScope.launch {
            DataPreloader.preloadMissions(database)
        }

        setContent {
            AventuraDosIdiomasTheme {
                val navController = rememberNavController()
                val viewModel: GameViewModel = viewModel(
                    factory = GameViewModelFactory(repository)
                )
                val uiState by viewModel.uiState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = if (uiState.currentUser == null) "welcome" else "map"
                ) {
                    composable("welcome") {
                        WelcomeScreen(
                            viewModel = viewModel,
                            onUserCreated = {
                                navController.navigate("map") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("map") {
                        MapScreen(
                            viewModel = viewModel,
                            onMissionSelected = { missionId ->
                                viewModel.startMission(missionId)
                                navController.navigate("mission")
                            }
                        )
                    }

                    composable("mission") {
                        MissionScreen(
                            viewModel = viewModel,
                            onBackPressed = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}