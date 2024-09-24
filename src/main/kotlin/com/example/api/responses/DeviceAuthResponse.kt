package com.example.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceAuthResponse(
    @SerialName("status")
    val status: ResponseStatus,
    @SerialName("token")
    val token: String?
)
