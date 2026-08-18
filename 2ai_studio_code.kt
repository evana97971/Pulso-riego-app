package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Lot

@Entity(tableName = "lots")
data class LotEntity(
  @PrimaryKey val id: String,
  val avgDrenaje: Double? = null,
  val overrideRecommendation: String? = null
) {
  fun toDomain(): Lot = Lot(
    id = id,
    avgDrenaje = avgDrenaje,
    overrideRecommendation = overrideRecommendation
  )

  companion object {
    fun fromDomain(lot: Lot): LotEntity = LotEntity(
      id = lot.id,
      avgDrenaje = lot.avgDrenaje,
      overrideRecommendation = lot.overrideRecommendation
    )
  }
}