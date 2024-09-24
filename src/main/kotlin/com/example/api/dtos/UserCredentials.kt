package com.example.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(
    val login: String,
    val password: String
)
