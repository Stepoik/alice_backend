package com.example.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceAuthRequest(
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("secret")
    val secret: String
)