package com.example.domain.repositories

import com.example.domain.models.AuthCode

interface AuthCodeRepository {
    suspend fun add(authCode: AuthCode)

    suspend fun get(clientId: String, code: String): AuthCode?

    suspend fun remove(clientId: String, code: String)
}