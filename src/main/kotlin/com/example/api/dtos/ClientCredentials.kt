package com.example.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientCredentials(
    @SerialName("client_secret")
    val secret: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_name")
    val name: String,
    @SerialName("redirect_uri")
    val redirectUri: String
)
