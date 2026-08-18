package com.example.pulsoapp.data

import com.example.pulsoapp.data.database.AppDatabase
import com.example.pulsoapp.data.database.LotEntity
import com.example.pulsoapp.data.database.PulseEntity
import com.example.pulsoapp.data.database.UserEntity
import com.example.pulsoapp.data.models.AppState
import com.example.pulsoapp.data.models.Lot
import com.example.pulsoapp.data.models.Pulse
import com.example.pulsoapp.data.models.Thresholds
import com.example.pulsoapp.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AppRepository(private val database: AppDatabase) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(
        AppState(
            users = DEFAULT_USERS,
            lots = DEFAULT_LOTS,
            pulses = DEFAULT_PULSES
        )
    )
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        scope.launch {
            // Initialize database
            val existingUsers = database.userDao().authenticate("admin", "admin")
            if (existingUsers == null) {
                database.userDao().insertAll(DEFAULT_USERS.map { it.toEntity() })
                database.lotDao().insertAll(DEFAULT_LOTS.map { it.toEntity() })
                database.pulseDao().insertAll(DEFAULT_PULSES.map { it.toEntity() })
            }

            // Observe changes
            combine(
                database.userDao().getAll(),
                database.lotDao().getAll(),
                database.pulseDao().getAll()
            ) { users, lots, pulses ->
                AppState(
                    users = users.map { it.toDomain() },
                    lots = lots.map { it.toDomain() },
                    pulses = pulses.map { it.toDomain() },
                    currentUser = _state.value.currentUser,
                    thresholds = _state.value.thresholds
                )
            }.collect { _state.value = it }
        }
    }

    fun authenticate(username: String, password: String): User? {
        val user = _state.value.users.find {
            (it.username == username || it.email == username) && it.password == password
        }
        if (user != null) {
            _state.value = _state.value.copy(currentUser = user)
        }
        return user
    }

    fun logout() {
        _state.value = _state.value.copy(currentUser = null)
    }

    companion object {
        private val DEFAULT_USERS = listOf(
            User("1", "admin", "Administrador", "admin@app.com", "admin", "ADMIN"),
            User("2", "ing_riego", "Ing. de Riego", "ing@app.com", "1234", "ENGINEER"),
            User("3", "regador", "Regador", "regador@app.com", "1234", "OPERATOR")
        )

        private val DEFAULT_LOTS = listOf(
            Lot("RA1", 5.0),
            Lot("RA2", 55.0),
            Lot("RA3", 38.0),
            Lot("RA4", null),
            Lot("RA5", null),
            Lot("RA6", null)
        )

        private val DEFAULT_PULSES = listOf(
            Pulse(1, "RA3", 600.0, 38.0, "07:10", "07:30"),
            Pulse(2, "RA1", 500.0, 5.0, "08:00", "08:20"),
            Pulse(3, "RA2", 700.0, 55.0, "09:00", "09:15")
        )
    }
}

private fun User.toEntity() = UserEntity(id, username, fullName, email, password, role)
private fun Lot.toEntity() = LotEntity(id, avgDrenaje)
private fun Pulse.toEntity() = PulseEntity(id, lote, sfr_ml, drenaje_pct, inicio, fin)