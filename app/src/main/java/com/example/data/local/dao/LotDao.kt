package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.LotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LotDao {
    @Query("SELECT * FROM lots ORDER BY id ASC")
    fun getAllLots(): Flow<List<LotEntity>>

    @Query("SELECT * FROM lots WHERE id = :id LIMIT 1")
    suspend fun getLotById(id: String): LotEntity?

    @Query("SELECT * FROM lots WHERE id = :id LIMIT 1")
    fun getLotFlowById(id: String): Flow<LotEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLots(lots: List<LotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLot(lot: LotEntity)

    @Update
    suspend fun updateLot(lot: LotEntity)

    @Query("DELETE FROM lots WHERE id = :id")
    suspend fun deleteLotById(id: String)

    @Query("DELETE FROM lots")
    suspend fun deleteAllLots()
}