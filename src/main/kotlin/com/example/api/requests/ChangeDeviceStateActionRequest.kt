package com.example.api.requests

import com.example.api.dtos.DeviceStateDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangeDeviceStateActionRequest(
    @SerialName("payload")
    val payload: ChangeDeviceStateActionPayload
)

@Serializable
data class ChangeDeviceStateActionPayload(
    @SerialName("devices")
    val devices: List<DeviceStateDto>
)

