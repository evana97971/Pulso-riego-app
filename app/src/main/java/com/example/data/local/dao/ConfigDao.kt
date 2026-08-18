package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): ConfigEntity?

    @Query("SELECT * FROM config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<ConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: ConfigEntity)

    @Query("DELETE FROM config")
    suspend fun deleteConfig()
}