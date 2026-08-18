package com.example.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.PulseViewModel
import com.example.ui.LotWithStats

@Composable
fun MainAppScreen(viewModel: PulseViewModel) {
    val lotsWithStats by viewModel.lotsWithStats.collectAsState()
    val state by viewModel.state.collectAsState()

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

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lotsWithStats) { lotStats ->
                LotCard(lotStats = lotStats)
            }
        }

        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun LotCard(lotStats: LotWithStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lote: ${lotStats.lot.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Estado: ${lotStats.status.label}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Drenaje Promedio: ${lotStats.computedAvgDrainage?.let { "$it%" } ?: "Sin datos"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Recomendación: ${lotStats.recommendation}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}