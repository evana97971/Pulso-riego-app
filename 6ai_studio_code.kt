package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PulseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PulseDao {
  @Query("SELECT * FROM pulses ORDER BY id DESC")
  fun getAllPulses(): Flow<List<PulseEntity>>

  @Query("SELECT * FROM pulses WHERE lote = :lotId ORDER BY id DESC")
  fun getPulsesByLot(lotId: String): Flow<List<PulseEntity>>

  @Query("SELECT * FROM pulses WHERE id = :id LIMIT 1")
  suspend fun getPulseById(id: Int): PulseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPulse(pulse: PulseEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPulses(pulses: List<PulseEntity>)

  @Update
  suspend fun updatePulse(pulse: PulseEntity)

  @Query("DELETE FROM pulses WHERE id = :id")
  suspend fun deletePulseById(id: Int)

  @Query("DELETE FROM pulses")
  suspend fun deleteAllPulses()

  @Query("SELECT COUNT(*) FROM pulses")
  fun getPulsesCount(): Flow<Int>
}