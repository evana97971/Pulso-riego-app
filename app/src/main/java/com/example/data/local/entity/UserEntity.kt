package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AppUser
import com.example.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val passwordHash: String,
    val roleName: String,
    val email: String,
    val phone: String,
    val active: Boolean = true,
    val createdAt: String = "2026-08-18"
) {
    fun toDomain(): AppUser {
        val role = try {
            UserRole.valueOf(roleName)
        } catch (_: Exception) {
            UserRole.REGADOR
        }
        return AppUser(
            id = id,
            username = username,
            fullName = fullName,
            passwordHash = passwordHash,
            role = role,
            email = email,
            phone = phone,
            active = active,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(user: AppUser): UserEntity = UserEntity(
            id = user.id,
            username = user.username,
            fullName = user.fullName,
            passwordHash = user.passwordHash,
            roleName = user.role.name,
            email = user.email,
            phone = user.phone,
            active = user.active,
            createdAt = user.createdAt
        )
    }
}