package com.example.api.dtos

import com.example.api.constants.AliceDeviceCapability
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DeviceCapability {
    val state: CapabilityState

    sealed interface CapabilityState {
        val instance: CapabilityInstance
    }

    @Serializable
    @SerialName(AliceDeviceCapability.ON_OFF)
    data class OnOffCapability(override val state: OnOffState) : DeviceCapability {
        @Serializable
        data class OnOffState(
            val value: Boolean,
            override val instance: CapabilityInstance.OnOffCapabilityInstance
        ) : CapabilityState
    }

    @Serializable
    @SerialName(AliceDeviceCapability.RANGE)
    data class RangeCapability(override val state: RangeState) : DeviceCapability {
        @Serializable
        data class RangeState(
            val value: Float,
            override val instance: CapabilityInstance.RangeCapabilityInstance
        ) : CapabilityState
    }
}