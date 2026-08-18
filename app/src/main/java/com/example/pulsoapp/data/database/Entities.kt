package com.example.pulsoapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pulsoapp.data.models.User
import com.example.pulsoapp.data.models.Lot
import com.example.pulsoapp.data.models.Pulse

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
) {
    fun toDomain() = User(id, username, fullName, email, password, role)
}

@Entity(tableName = "lots")
data class LotEntity(
    @PrimaryKey val id: String,
    val avgDrenaje: Double? = null
) {
    fun toDomain() = Lot(id, avgDrenaje)
}

@Entity(tableName = "pulses")
data class PulseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lote: String,
    val sfr_ml: Double,
    val drenaje_pct: Double,
    val inicio: String,
    val fin: String
) {
    fun toDomain() = Pulse(id, lote, sfr_ml, drenaje_pct, inicio, fin)
}