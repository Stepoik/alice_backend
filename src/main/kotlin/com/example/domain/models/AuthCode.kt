package com.example.domain.models

data class AuthCode(
    val code: String,
    val clientId: String,
    val userId: String
)
