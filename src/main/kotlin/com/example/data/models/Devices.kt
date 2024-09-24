package com.example.data.models

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Devices: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = varchar("description", 255)
    val type = varchar("type", 255)
    val userId = varchar("user_id", 36).nullable()
    val isOn = bool("is_on")
    val temperature = float("selected_temperature")
    val secret = varchar("secret", 36)

    val userIdForeignKey = foreignKey(userId to Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(id)
}