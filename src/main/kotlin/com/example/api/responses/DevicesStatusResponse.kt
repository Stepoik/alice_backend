package com.example.api.responses

import com.example.api.dtos.DeviceStateDto
import kotlinx.serialization.Serializable

// TODO: Сделать норм мапперы и убрать всякую логику отсюда

@Serializable
data class DevicesStatusResponse(
    val requestId: String,
    val payload: DevicesStatusPayload
) {
    constructor(requestId: String, devices: List<DeviceStateDto>): this(
        requestId = requestId,
        payload = DevicesStatusPayload(devices)
    )
}

@Serializable
data class DevicesStatusPayload(
    val devices: List<DeviceStateDto>
)
