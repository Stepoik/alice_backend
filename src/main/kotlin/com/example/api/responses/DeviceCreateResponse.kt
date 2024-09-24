package com.example.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCreateResponse(
    @SerialName("status")
    val status: ResponseStatus,
    @SerialName("secret")
    val secret: String? = null,
    @SerialName("id")
    val id: String? = null
)
