package com.example.data.models

import org.jetbrains.exposed.sql.Table

object AuthClients : Table() {
    val clientId = varchar("id", length = 25)
    val secret = varchar("secret", 36)
    val redirectUri = text("redirect_uri")
    val name = varchar("name", length = 50)

    override val primaryKey = PrimaryKey(clientId)
}
