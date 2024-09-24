package com.example.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    @SerialName("username")
    val username: String,
    @SerialName("login")
    val login: String,
    @SerialName("password")
    val password: String
)
