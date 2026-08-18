package com.example.model

enum class UserRole(val label: String, val canEdit: Boolean) {
  ADMIN("Administrador", true),
  ING_RIEGO("Ing. de Riego", true),
  ING_APLICACION("Ing. de Aplicación", true),
  ING_CAMPO("Ing. de Campo", true),
  ING_FUNDO("Ing. de Fundo", true),
  REGADOR("Regador", false)
}

data class AppUser(
  val id: String,
  val username: String,
  val fullName: String,
  val passwordHash: String,
  val role: UserRole,
  val email: String,
  val phone: String,
  val active: Boolean = true,
  val createdAt: String = "2026-08-18"
)

data class Pulse(
  val id: Int,
  val lote: String,
  val sfr_ml: Double,
  val drenaje_pct: Double,
  val inicio: String,
  val fin: String,
  val fecha: String = "2026-08-18"
)

data class Lot(
  val id: String,
  val avgDrenaje: Double? = null,
  val overrideRecommendation: String? = null
)

data class Thresholds(
  val high: Double = 40.0,
  val low: Double = 10.0
)

enum class DrainageStatus(val label: String) {
  OPTIMO("Óptimo"),
  EXCESO("Exceso de Drenaje"),
  DEFICIT("Déficit de Drenaje"),
  SIN_DATOS("Sin Datos")
}

data class AlertItem(
  val id: String,
  val lotId: String,
  val timestamp: String,
  val pulseId: Int,
  val drainagePct: Double,
  val status: DrainageStatus,
  val message: String,
  val recommendation: String,
  val sfrAppliedMl: Double,
  val drainedVolumeMl: Double
)

data class MonitoringState(
  val pulses: List<Pulse> = emptyList(),
  val lots: List<Lot> = emptyList(),
  val users: List<AppUser> = emptyList(),
  val currentUser: AppUser? = null,
  val thresholds: Thresholds = Thresholds()
)