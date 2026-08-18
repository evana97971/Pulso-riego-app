package com.example.pulsoapp

import android.app.Application
import com.example.data.PulseRepository
import com.example.data.local.AppDatabase

class PulseApplication : Application() {
    private lateinit var _database: AppDatabase
    private lateinit var _repository: PulseRepository

    val database: AppDatabase
        get() = _database

    val repository: PulseRepository
        get() = _repository

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize database
        _database = AppDatabase.getInstance(this)
        
        // Initialize repository
        _repository = PulseRepository(_database)
    }

    companion object {
        lateinit var instance: PulseApplication
            private set
    }
}