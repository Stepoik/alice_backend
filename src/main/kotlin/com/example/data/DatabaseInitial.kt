package com.example.data

import com.example.data.models.AuthClients
import com.example.data.models.Devices
import com.example.data.models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseInitial {
    fun initDatabase(database: Database) {
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(AuthClients)
            SchemaUtils.create(Devices)
        }
    }
}