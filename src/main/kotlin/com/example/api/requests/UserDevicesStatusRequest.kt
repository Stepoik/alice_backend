package com.example.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDevicesStatusRequest(
    @SerialName("devices")
    val devices: List<DeviceDto>
)

@Serializable
data class DeviceDto(
    @SerialName("id")
    val id: String
)
