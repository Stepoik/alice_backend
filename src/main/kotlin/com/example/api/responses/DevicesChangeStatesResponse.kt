package com.example.api.responses

import com.example.api.dtos.CapabilityInstance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DevicesChangeStatesResponse(
    @SerialName("request_id")
    val requestId: String,
    @SerialName("payload")
    val payload: DevicesChangeStatesPayload
)

@Serializable
data class DevicesChangeStatesPayload(
    @SerialName("devices")
    val devices: List<DeviceChangeStateResult>
)

@Serializable
data class DeviceChangeStateResult(
    @SerialName("id")
    val id: String,
    @SerialName("capabilities")
    val capabilities: List<CapabilityChangeResult>
)

@Serializable
data class CapabilityChangeResult(
    @SerialName("type")
    val type: String,
    @SerialName("state")
    val state: CapabilityChangeResultState
)

@Serializable
data class CapabilityChangeResultState(
    @SerialName("instance")
    val instance: CapabilityInstance,
    @SerialName("action_result")
    val actionResult: ActionResult
)

@Serializable
data class ActionResult(
    @SerialName("status")
    val status: String,
    @SerialName("error_code")
    val errorCode: String? = null,
    @SerialName("error_message")
    val errorMessage: String? = null
) {
    companion object {
        val SUCCESS_RESULT = ActionResult(status = "DONE")
    }
}
