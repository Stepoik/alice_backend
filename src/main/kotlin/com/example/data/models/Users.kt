package com.example.data.models

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = varchar("id", 36)
    val name = varchar("name", 255)
    val login = varchar("login", 50).uniqueIndex()
    val password = varchar("password", 64)

    override val primaryKey = PrimaryKey(id)
}