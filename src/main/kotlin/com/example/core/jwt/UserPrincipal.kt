package com.example.core.jwt

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserPrincipal(
    val userId: String,
    val userName: String
): Principal