package com.example.pulsoapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun authenticate(username: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
}

@Dao
interface LotDao {
    @Query("SELECT * FROM lots")
    fun getAll(): Flow<List<LotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lots: List<LotEntity>)
}

@Dao
interface PulseDao {
    @Query("SELECT * FROM pulses")
    fun getAll(): Flow<List<PulseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pulse: PulseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pulses: List<PulseEntity>)
}