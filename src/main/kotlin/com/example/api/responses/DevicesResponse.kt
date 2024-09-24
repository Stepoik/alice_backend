package com.example.api.responses

import com.example.api.constants.AliceDeviceCapability
import com.example.domain.models.Device
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DevicesResponse(
    @SerialName("request_id")
    val requestId: String,
    @SerialName("payload")
    val payload: UserDevicesDto
) {
    constructor(requestId: String, userId: String, devices: List<Device>) : this(
        requestId = requestId,
        payload = UserDevicesDto(userId, devices)
    )
}

@Serializable
data class UserDevicesDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("devices")
    val devices: List<DeviceDto>
) {
    // Последний аргумент просто чтоб jvm не ругался
    constructor(userId: String, devices: List<Device>, fromDomain: Boolean = true) : this(
        userId = userId,
        devices = devices.map(::DeviceDto)
    )
}

@Serializable
data class DeviceDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("type")
    val type: String,
    @SerialName("capabilities")
    val capabilities: List<ProvidedDeviceCapability>
) {
    constructor(device: Device) : this(
        id = device.id.toString(),
        name = device.name,
        description = device.description,
        type = device.type,
        capabilities = listOf(
            ProvidedDeviceCapability(AliceDeviceCapability.ON_OFF),
            ProvidedDeviceCapability(
                AliceDeviceCapability.RANGE,
                parameters = DeviceCapabilityParameters("temperature", unit = "unit.temperature.celsius", range = DeviceRange(min = 18f, max = 28f))
            )
        )
    )
}

@Serializable
data class ProvidedDeviceCapability(
    val type: String,
    val parameters: DeviceCapabilityParameters? = null
)

@Serializable
data class DeviceCapabilityParameters(
    val instance: String,
    val unit: String,
    val range: DeviceRange
)

@Serializable
data class DeviceRange(
    val min: Float,
    val max: Float
)