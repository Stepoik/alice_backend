package com.example.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BindDeviceRequest(
    @SerialName("device_id")
    val deviceId: Int,
)
