package com.example.data.repositories

import com.example.data.models.AuthClients
import com.example.data.models.Devices
import com.example.data.models.Users
import com.example.domain.models.AuthClient
import com.example.domain.repositories.AuthClientRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AuthClientRepositoryImpl(
    private val database: Database
) : AuthClientRepository, BaseRepository<AuthClients>() {

    override suspend fun getClientById(clientId: String): AuthClient? {
        return dbQuery {
            AuthClients.select {
                AuthClients.clientId eq clientId
            }.map(ResultRow::toAuthClient)
                .firstOrNull()
        }
    }

    override suspend fun saveClient(client: AuthClient) {
        dbQuery {
            AuthClients.insert {
                it[clientId] = client.clientId
                it[secret] = client.secret
                it[name] = client.name
                it[redirectUri] = client.redirectUri
            }
        }
    }
}

private fun ResultRow.toAuthClient() = AuthClient(
    clientId = this[AuthClients.clientId],
    secret = this[AuthClients.secret],
    name = this[AuthClients.name],
    redirectUri = this[AuthClients.redirectUri]
)