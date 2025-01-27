package com.example.aventuradosidiomas.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val selectedLanguage: String,
    val points: Int = 0,
    val level: Int = 1,
    val avatarCustomization: String = "" // JSON string com customizações do avatar
) 