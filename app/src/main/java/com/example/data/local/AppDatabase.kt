package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ConfigDao
import com.example.data.local.dao.LotDao
import com.example.data.local.dao.PulseDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ConfigEntity
import com.example.data.local.entity.LotEntity
import com.example.data.local.entity.PulseEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        LotEntity::class,
        PulseEntity::class,
        UserEntity::class,
        ConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lotDao(): LotDao
    abstract fun pulseDao(): PulseDao
    abstract fun userDao(): UserDao
    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pulsos_riego_database.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun createInMemory(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}