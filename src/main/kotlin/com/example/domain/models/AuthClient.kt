package com.example.domain.models

data class AuthClient(
    val clientId: String,
    val secret: String,
    val name: String,
    val redirectUri: String
)
