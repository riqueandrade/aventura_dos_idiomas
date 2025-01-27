package com.example.aventuradosidiomas.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_progress",
    primaryKeys = ["userId", "missionId"],
    indices = [
        Index("missionId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Mission::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProgress(
    val userId: Long,
    val missionId: Long,
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val attempts: Int = 0,
    val lastAttemptDate: Long? = null
) 