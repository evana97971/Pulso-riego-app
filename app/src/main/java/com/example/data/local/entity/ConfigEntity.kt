package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey val id: Int = 1,
    val highThreshold: Double = 40.0,
    val lowThreshold: Double = 10.0,
    val currentUserId: String? = null
)