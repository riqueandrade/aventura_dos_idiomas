package com.example.aventuradosidiomas.data.util

import com.example.aventuradosidiomas.data.AppDatabase
import com.example.aventuradosidiomas.data.entity.Mission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object DataPreloader {
    suspend fun preloadMissions(database: AppDatabase) = withContext(Dispatchers.IO) {
        try {
            // Verifica se já existem missões no banco de forma mais segura
            val existingMissions = try {
                database.missionDao().getAllCategories().first()
            } catch (e: Exception) {
                emptyList()
            }
            
            if (existingMissions.isNotEmpty()) {
                return@withContext
            }

            // Vocabulário - Saudações em Espanhol
            val greetingsMissions = listOf(
                Mission(
                    title = "Saludos Básicos",
                    description = "¡Aprende los saludos más comunes!",
                    category = "Saudações",
                    type = "VOCABULARY",
                    difficulty = 1,
                    pointsReward = 100,
                    content = JSONObject().apply {
                        put("word", "Hello")
                        put("options", JSONArray().apply {
                            put("Hola")
                            put("Adiós")
                            put("Buenos días")
                            put("Buenas noches")
                        })
                        put("correct_answer", "Hola")
                    }.toString(),
                    isUnlocked = true
                ),
                Mission(
                    title = "Despedidas",
                    description = "¡Aprende a despedirte!",
                    category = "Saudações",
                    type = "VOCABULARY",
                    difficulty = 1,
                    pointsReward = 100,
                    content = JSONObject().apply {
                        put("word", "Goodbye")
                        put("options", JSONArray().apply {
                            put("Hola")
                            put("Adiós")
                            put("Buenos días")
                            put("Buenas noches")
                        })
                        put("correct_answer", "Adiós")
                    }.toString(),
                    isUnlocked = true
                )
            )

            // Vocabulário - Comida em Espanhol
            val foodMissions = listOf(
                Mission(
                    title = "Frutas",
                    description = "¡Aprende los nombres de las frutas más comunes!",
                    category = "Comida",
                    type = "VOCABULARY",
                    difficulty = 1,
                    pointsReward = 100,
                    content = JSONObject().apply {
                        put("word", "Apple")
                        put("options", JSONArray().apply {
                            put("Manzana")
                            put("Plátano")
                            put("Naranja")
                            put("Uva")
                        })
                        put("correct_answer", "Manzana")
                    }.toString(),
                    isUnlocked = true
                ),
                Mission(
                    title = "Bebidas",
                    description = "¡Aprende los nombres de las bebidas!",
                    category = "Comida",
                    type = "VOCABULARY",
                    difficulty = 1,
                    pointsReward = 100,
                    content = JSONObject().apply {
                        put("word", "Water")
                        put("options", JSONArray().apply {
                            put("Agua")
                            put("Jugo")
                            put("Refresco")
                            put("Café")
                        })
                        put("correct_answer", "Agua")
                    }.toString(),
                    isUnlocked = true
                )
            )

            // Gramática em Espanhol
            val grammarMissions = listOf(
                Mission(
                    title = "Verbo Ser/Estar",
                    description = "¡Practica el uso de los verbos 'ser' y 'estar'!",
                    category = "Gramática",
                    type = "GRAMMAR",
                    difficulty = 2,
                    pointsReward = 150,
                    content = JSONObject().apply {
                        put("sentence", "Completa: Yo ___ estudiante. (ser)")
                        put("correct_answer", "soy")
                    }.toString(),
                    isUnlocked = true
                ),
                Mission(
                    title = "Artículos",
                    description = "¡Aprende a usar los artículos 'el' y 'la'!",
                    category = "Gramática",
                    type = "GRAMMAR",
                    difficulty = 2,
                    pointsReward = 150,
                    content = JSONObject().apply {
                        put("sentence", "Completa: ___ manzana es roja.")
                        put("correct_answer", "la")
                    }.toString(),
                    isUnlocked = true
                )
            )

            // Compreensão em Espanhol
            val comprehensionMissions = listOf(
                Mission(
                    title = "Diálogo Simple",
                    description = "¡Entiende un diálogo básico!",
                    category = "Compreensão",
                    type = "COMPREHENSION",
                    difficulty = 3,
                    pointsReward = 200,
                    content = JSONObject().apply {
                        put("text", """
                            Juan: ¡Hola! ¿Cómo estás?
                            María: Muy bien, gracias. ¿Y tú?
                            Juan: Bien también. ¡Que tengas un buen día!
                            María: ¡Igualmente!
                        """.trimIndent())
                        put("question", "¿Cómo está María?")
                        put("options", JSONArray().apply {
                            put("Muy bien")
                            put("Mal")
                            put("Cansada")
                            put("Con hambre")
                        })
                        put("correct_answer", "Muy bien")
                    }.toString(),
                    isUnlocked = true
                )
            )

            // Inserir todas as missões em uma única transação
            database.runInTransaction {
                val allMissions = greetingsMissions + foodMissions + grammarMissions + comprehensionMissions
                allMissions.forEach { mission ->
                    database.missionDao().insertMission(mission)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Propaga o erro para ser tratado na camada superior
        }
    }
} 