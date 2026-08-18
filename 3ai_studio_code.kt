package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Pulse

@Entity(tableName = "pulses")
data class PulseEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val lote: String,
  val sfr_ml: Double,
  val drenaje_pct: Double,
  val inicio: String,
  val fin: String,
  val fecha: String = "2026-08-18"
) {
  fun toDomain(): Pulse = Pulse(
    id = id,
    lote = lote,
    sfr_ml = sfr_ml,
    drenaje_pct = drenaje_pct,
    inicio = inicio,
    fin = fin,
    fecha = fecha
  )

  companion object {
    fun fromDomain(pulse: Pulse): PulseEntity = PulseEntity(
      id = pulse.id,
      lote = pulse.lote,
      sfr_ml = pulse.sfr_ml,
      drenaje_pct = pulse.drenaje_pct,
      inicio = pulse.inicio,
      fin = pulse.fin,
      fecha = pulse.fecha
    )
  }
}