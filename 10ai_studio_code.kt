package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.PulseApplication
import com.example.data.PulseRepository
import com.example.model.AlertItem
import com.example.model.AppUser
import com.example.model.DrainageStatus
import com.example.model.Lot
import com.example.model.MonitoringState
import com.example.model.Pulse
import com.example.model.Thresholds
import com.example.model.UserRole
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LotWithStats(
  val lot: Lot,
  val computedAvgDrainage: Double?,
  val status: DrainageStatus,
  val pulseCount: Int,
  val lastPulse: Pulse?,
  val recommendation: String
)

class PulseViewModel(
  private val repository: PulseRepository = PulseApplication.instance.repository
) : ViewModel() {

  val state: StateFlow<MonitoringState> = repository.state

  val lotsWithStats: StateFlow<List<LotWithStats>> = state.map { s ->
    s.lots.map { lot ->
      val lotPulses = s.pulses.filter { it.lote == lot.id }
      val avg = if (lotPulses.isNotEmpty()) {
        lotPulses.map { it.drenaje_pct }.average()
      } else {
        lot.avgDrenaje
      }

      val status = when {
        avg == null -> DrainageStatus.SIN_DATOS
        avg > s.thresholds.high -> DrainageStatus.EXCESO
        avg < s.thresholds.low -> DrainageStatus.DEFICIT
        else -> DrainageStatus.OPTIMO
      }

      val rec = lot.overrideRecommendation ?: when (status) {
        DrainageStatus.EXCESO -> "Reducir tiempo de riego en 10-15% para el siguiente turno"
        DrainageStatus.DEFICIT -> "Aumentar volumen SFR o frecuencia de riego"
        DrainageStatus.OPTIMO -> "Mantener programa y turno actual"
        DrainageStatus.SIN_DATOS -> "Registrar primer pulso para calibración"
      }

      LotWithStats(
        lot = lot,
        computedAvgDrainage = avg,
        status = status,
        pulseCount = lotPulses.size,
        lastPulse = lotPulses.firstOrNull(),
        recommendation = rec
      )
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun login(usernameOrEmail: String, password: String): Boolean {
    val user = repository.authenticate(usernameOrEmail, password)
    return user != null
  }

  fun logout() {
    repository.logout()
  }

  fun registerPulse(
    lote: String,
    sfrMl: Double,
    drenajePct: Double,
    inicio: String,
    fin: String
  ): Pulse {
    return repository.addPulse(lote, sfrMl, drenajePct, inicio, fin)
  }

  fun updateThresholds(high: Double, low: Double) {
    repository.updateThresholds(high, low)
  }
}