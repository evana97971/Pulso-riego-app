package com.example.pulsoapp.data.models

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
)

data class Lot(
    val id: String,
    val avgDrenaje: Double? = null
)

data class Pulse(
    val id: Int = 0,
    val lote: String,
    val sfr_ml: Double,
    val drenaje_pct: Double,
    val inicio: String,
    val fin: String
)

data class Thresholds(
    val high: Double = 40.0,
    val low: Double = 10.0
)

data class AppState(
    val users: List<User> = emptyList(),
    val lots: List<Lot> = emptyList(),
    val pulses: List<Pulse> = emptyList(),
    val currentUser: User? = null,
    val thresholds: Thresholds = Thresholds()
)