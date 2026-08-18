package com.example.pulsoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.PulseRepository
import com.example.data.local.AppDatabase
import com.example.model.DrainageStatus
import com.example.model.MonitoringState
import com.example.ui.theme.PulsosDeRiegoTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: PulseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar base de datos y repositorio
        database = AppDatabase.getInstance(this)
        repository = PulseRepository(database)
        
        enableEdgeToEdge()
        setContent {
            PulsosDeRiegoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PulsosRiegoApp(repository)
                }
            }
        }
    }
}

@Composable
fun PulsosRiegoApp(repository: PulseRepository) {
    val state by repository.state.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (state.currentUser == null) {
        // Pantalla de Login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pulso de Riego",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario o Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor completa todos los campos"
                    } else if (repository.authenticate(username, password) != null) {
                        errorMessage = ""
                    } else {
                        errorMessage = "Usuario o contraseña incorrectos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Iniciar Sesión")
            }
        }
    } else {
        // Pantalla Principal
        MainAppScreen(state, repository)
    }
}

@Composable
fun MainAppScreen(state: MonitoringState, repository: PulseRepository) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Bienvenido, ${state.currentUser?.fullName}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.lots.forEach { lot ->
                val lotPulses = state.pulses.filter { it.lote == lot.id }
                val avg = if (lotPulses.isNotEmpty()) {
                    lotPulses.map { it.drenaje_pct }.average()
                } else {
                    lot.avgDrenaje
                }

                val status = when {
                    avg == null -> DrainageStatus.SIN_DATOS
                    avg > state.thresholds.high -> DrainageStatus.EXCESO
                    avg < state.thresholds.low -> DrainageStatus.DEFICIT
                    else -> DrainageStatus.OPTIMO
                }

                val rec = lot.overrideRecommendation ?: when (status) {
                    DrainageStatus.EXCESO -> "Reducir tiempo de riego en 10-15% para el siguiente turno"
                    DrainageStatus.DEFICIT -> "Aumentar volumen SFR o frecuencia de riego"
                    DrainageStatus.OPTIMO -> "Mantener programa y turno actual"
                    DrainageStatus.SIN_DATOS -> "Registrar primer pulso para calibración"
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lote: ${lot.id}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Estado: ${status.label}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Drenaje Promedio: ${avg?.let { String.format("%.2f%%", it) } ?: "Sin datos"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Pulsos: ${lotPulses.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Recomendación: $rec",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        Button(
            onClick = { repository.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}