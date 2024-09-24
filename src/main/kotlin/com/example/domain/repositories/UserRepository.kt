package com.example.domain.repositories

import com.example.domain.models.User

interface UserRepository {
    suspend fun getUserByLogin(login: String): User?

    suspend fun getUserById(id: String): User?

    suspend fun createUser(user: User): User
}