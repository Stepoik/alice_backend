package com.example.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceStateDto(
    @SerialName("id")
    val deviceId: String,
    @SerialName("capabilities")
    val capabilities: List<DeviceCapability>
)
