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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.PulseRepository
import com.example.data.local.AppDatabase
import com.example.model.DrainageStatus
import com.example.ui.theme.PulsosDeRiegoTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: PulseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Inicializar base de datos y repositorio
            val database = AppDatabase.getInstance(this)
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
        } catch (e: Exception) {
            e.printStackTrace()
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
        LoginView(
            username = username,
            password = password,
            errorMessage = errorMessage,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLogin = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor completa todos los campos"
                } else {
                    val user = repository.authenticate(username, password)
                    if (user != null) {
                        errorMessage = ""
                    } else {
                        errorMessage = "Usuario o contraseña incorrectos"
                    }
                }
            }
        )
    } else {
        MainView(state, repository)
    }
}

@Composable
fun LoginView(
    username: String,
    password: String,
    errorMessage: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
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
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Usuario o Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Composable
fun MainView(state: com.example.model.MonitoringState, repository: PulseRepository) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Bienvenido, ${state.currentUser?.fullName}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.lots) { lot ->
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

                val statusColor = when (status) {
                    DrainageStatus.OPTIMO -> Color.Green
                    DrainageStatus.EXCESO -> Color.Red
                    DrainageStatus.DEFICIT -> Color(0xFFFFA500) // Orange
                    DrainageStatus.SIN_DATOS -> Color.Gray
                }

                val rec = lot.overrideRecommendation ?: when (status) {
                    DrainageStatus.EXCESO -> "Reducir tiempo de riego en 10-15%"
                    DrainageStatus.DEFICIT -> "Aumentar volumen SFR o frecuencia"
                    DrainageStatus.OPTIMO -> "Mantener programa actual"
                    DrainageStatus.SIN_DATOS -> "Registrar primer pulso"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lote: ${lot.id}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Estado: ${status.label}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = statusColor
                        )
                        Text(
                            text = "Drenaje: ${avg?.let { String.format("%.2f%%", it) } ?: "Sin datos"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Pulsos: ${lotPulses.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Recom: $rec",
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