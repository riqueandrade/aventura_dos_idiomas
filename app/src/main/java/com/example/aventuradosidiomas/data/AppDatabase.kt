package com.example.aventuradosidiomas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aventuradosidiomas.data.dao.MissionDao
import com.example.aventuradosidiomas.data.dao.UserDao
import com.example.aventuradosidiomas.data.dao.UserProgressDao
import com.example.aventuradosidiomas.data.entity.Mission
import com.example.aventuradosidiomas.data.entity.User
import com.example.aventuradosidiomas.data.entity.UserProgress

@Database(
    entities = [
        User::class,
        Mission::class,
        UserProgress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun missionDao(): MissionDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aventura_dos_idiomas.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 