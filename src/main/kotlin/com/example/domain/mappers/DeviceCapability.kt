package com.example.domain.mappers

import com.example.api.constants.AliceDeviceCapability
import com.example.api.dtos.CapabilityInstance
import com.example.api.dtos.DeviceCapability
import com.example.domain.models.Device
import com.example.domain.models.DeviceAction
import com.example.domain.models.DeviceState

fun DeviceState.toDeviceAction(): DeviceAction {
    return DeviceAction(
        temperature = this.temperature,
        isOn = this.isOn,
    )
}

fun DeviceCapability.toCapabilityType(): String {
    return when (this) {
        is DeviceCapability.OnOffCapability -> {
            AliceDeviceCapability.ON_OFF
        }

        is DeviceCapability.RangeCapability -> {
            AliceDeviceCapability.RANGE
        }
    }
}