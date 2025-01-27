package com.example.aventuradosidiomas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aventuradosidiomas.data.entity.Mission
import com.example.aventuradosidiomas.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionScreen(
    viewModel: GameViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val mission = uiState.currentMission
    var currentScore by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf(false) }

    // Coletar eventos do ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.MissionCompleted -> {
                    delay(1000) // Pequeno delay para mostrar o feedback
                    onBackPressed()
                }
                is GameEvent.Error -> {
                    feedbackMessage = event.message
                    showFeedback = true
                }
                else -> {}
            }
        }
    }

    if (mission == null) {
        onBackPressed()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mission.title) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Descrição da missão
            Text(
                text = mission.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (mission.type) {
                "VOCABULARY" -> VocabularyMission(
                    mission = mission,
                    onAnswer = { correct, score ->
                        currentScore = score
                        showFeedback = true
                        isCorrect = correct
                        feedbackMessage = if (correct) "¡Correcto!" else "¡Inténtalo de nuevo!"
                    }
                )
                "GRAMMAR" -> GrammarMission(
                    mission = mission,
                    onAnswer = { correct, score ->
                        currentScore = score
                        showFeedback = true
                        isCorrect = correct
                        feedbackMessage = if (correct) "¡Correcto!" else "¡Inténtalo de nuevo!"
                    }
                )
                "COMPREHENSION" -> ComprehensionMission(
                    mission = mission,
                    onAnswer = { correct, score ->
                        currentScore = score
                        showFeedback = true
                        isCorrect = correct
                        feedbackMessage = if (correct) "¡Correcto!" else "¡Inténtalo de nuevo!"
                    }
                )
            }

            if (showFeedback) {
                FeedbackDialog(
                    message = feedbackMessage,
                    isCorrect = isCorrect,
                    score = currentScore,
                    onDismiss = {
                        if (isCorrect) {
                            viewModel.completeMission(currentScore)
                        } else {
                            showFeedback = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun VocabularyMission(
    mission: Mission,
    onAnswer: (Boolean, Int) -> Unit
) {
    val content = remember { JSONObject(mission.content) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Palavra a ser traduzida
        Text(
            text = content.getString("word"),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Opções de resposta
        val options = content.getJSONArray("options")
        for (i in 0 until options.length()) {
            val option = options.getString(i)
            OutlinedButton(
                onClick = { selectedAnswer = option },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAnswer == option)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val isCorrect = selectedAnswer == content.getString("correct_answer")
                val score = if (isCorrect) mission.pointsReward else 0
                onAnswer(isCorrect, score)
            },
            enabled = selectedAnswer != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar")
        }
    }
}

@Composable
fun GrammarMission(
    mission: Mission,
    onAnswer: (Boolean, Int) -> Unit
) {
    val content = remember { JSONObject(mission.content) }
    var answer by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = content.getString("sentence"),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Sua resposta") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val isCorrect = answer.trim().equals(
                    content.getString("correct_answer").trim(),
                    ignoreCase = true
                )
                val score = if (isCorrect) mission.pointsReward else 0
                onAnswer(isCorrect, score)
            },
            enabled = answer.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar")
        }
    }
}

@Composable
fun ComprehensionMission(
    mission: Mission,
    onAnswer: (Boolean, Int) -> Unit
) {
    val content = remember { JSONObject(mission.content) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = content.getString("text"),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = content.getString("question"),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = content.getJSONArray("options")
        for (i in 0 until options.length()) {
            val option = options.getString(i)
            OutlinedButton(
                onClick = { selectedAnswer = option },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAnswer == option)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val isCorrect = selectedAnswer == content.getString("correct_answer")
                val score = if (isCorrect) mission.pointsReward else 0
                onAnswer(isCorrect, score)
            },
            enabled = selectedAnswer != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar")
        }
    }
}

@Composable
fun FeedbackDialog(
    message: String,
    isCorrect: Boolean,
    score: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isCorrect)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(message)
        },
        text = {
            if (isCorrect) {
                Text("Você ganhou $score pontos!")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isCorrect) "Continuar" else "Tentar Novamente")
            }
        }
    )
} 