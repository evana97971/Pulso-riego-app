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
import com.example.pulsoapp.data.AppRepository
import com.example.pulsoapp.data.database.AppDatabase
import com.example.pulsoapp.ui.theme.PulsosDeRiegoTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(this)
        repository = AppRepository(database)

        setContent {
            PulsosDeRiegoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(repository)
                }
            }
        }
    }
}

@Composable
fun AppContent(repository: AppRepository) {
    val state by repository.state.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (state.currentUser == null) {
        LoginScreen(
            username = username,
            password = password,
            errorMessage = errorMessage,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLogin = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Completa todos los campos"
                } else {
                    val user = repository.authenticate(username, password)
                    if (user != null) {
                        errorMessage = ""
                    } else {
                        errorMessage = "Credenciales inválidas"
                    }
                }
            }
        )
    } else {
        DashboardScreen(state, repository)
    }
}

@Composable
fun LoginScreen(
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Pulso de Riego",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Usuario o Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Composable
fun DashboardScreen(
    state: com.example.pulsoapp.data.models.AppState,
    repository: AppRepository
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Bienvenido, ${state.currentUser?.fullName}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.lots) { lot ->
                val pulses = state.pulses.filter { it.lote == lot.id }
                val avg = if (pulses.isNotEmpty()) {
                    pulses.map { it.drenaje_pct }.average()
                } else {
                    lot.avgDrenaje ?: 0.0
                }

                val status = when {
                    avg > state.thresholds.high -> "EXCESO"
                    avg < state.thresholds.low -> "DÉFICIT"
                    else -> "ÓPTIMO"
                }

                val statusColor = when (status) {
                    "ÓPTIMO" -> Color.Green
                    "EXCESO" -> Color.Red
                    else -> Color(0xFFFFA500)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Lote ${lot.id}", style = MaterialTheme.typography.titleSmall)
                        Text("Estado: $status", color = statusColor, style = MaterialTheme.typography.bodySmall)
                        Text("Drenaje: ${String.format("%.1f%%", avg)}", style = MaterialTheme.typography.bodySmall)
                        Text("Pulsos: ${pulses.size}", style = MaterialTheme.typography.bodySmall)
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