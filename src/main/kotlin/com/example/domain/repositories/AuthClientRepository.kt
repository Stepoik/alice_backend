package com.example.domain.repositories

import com.example.domain.models.AuthClient

interface AuthClientRepository {
    suspend fun getClientById(clientId: String): AuthClient?

    suspend fun saveClient(client: AuthClient)
}