package com.example.aventuradosidiomas.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // ex: "Comida", "Saudações", etc.
    val type: String, // "VOCABULARY", "GRAMMAR", "COMPREHENSION"
    val difficulty: Int, // 1 a 5
    val pointsReward: Int,
    val content: String, // JSON string com o conteúdo da missão
    val requiredLevel: Int = 1,
    val isUnlocked: Boolean = false
) 