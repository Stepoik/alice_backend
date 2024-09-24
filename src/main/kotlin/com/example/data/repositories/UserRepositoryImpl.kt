package com.example.data.repositories

import com.example.data.models.AuthClients
import com.example.data.models.Devices
import com.example.data.models.Users
import com.example.domain.models.User
import com.example.domain.repositories.UserRepository
import kotlinx.html.InputType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl(
    private val database: Database
): UserRepository, BaseRepository<Users>() {

    override suspend fun getUserByLogin(login: String): User? {
        return dbQuery {
            Users.select {
                Users.login eq login
            }.map(ResultRow::toUser)
                .firstOrNull()
        }
    }

    override suspend fun getUserById(id: String): User? {
        return dbQuery {
            Users.select {
                Users.id eq id
            }.map(ResultRow::toUser)
                .firstOrNull()
        }
    }

    override suspend fun createUser(user: User): User {
        return dbQuery {
            val userId = Users.insert {
                it[id] = user.id
                it[name] = user.name
                it[login] = user.login
                it[password] = user.password
            } get Users.id
            Users.select { Users.id eq userId }.map(ResultRow::toUser).first()
        }
    }
}

private fun ResultRow.toUser(): User {
    return User(
        id = this[Users.id],
        login = this[Users.login],
        name = this[Users.name],
        password = this[Users.password],
    )
}