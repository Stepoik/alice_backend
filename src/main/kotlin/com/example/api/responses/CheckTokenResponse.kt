package com.example.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckTokenResponse(
    @SerialName("ok")
    val ok: Boolean,
    @SerialName("error")
    val error: String
)
