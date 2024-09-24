package com.example.domain.services

import com.example.api.constants.AliceDeviceCapability
import com.example.api.dtos.CapabilityInstance
import com.example.api.dtos.DeviceCapability
import com.example.api.dtos.DeviceStateDto
import com.example.api.responses.ActionResult
import com.example.api.responses.CapabilityChangeResult
import com.example.api.responses.CapabilityChangeResultState
import com.example.api.responses.DeviceChangeStateResult
import com.example.domain.exceptions.DeviceNotFoundException
import com.example.domain.exceptions.UserNotFoundException
import com.example.domain.mappers.toCapabilityType
import com.example.domain.mappers.toDeviceAction
import com.example.domain.models.*
import com.example.domain.repositories.DeviceActionRepository
import com.example.domain.repositories.DeviceRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AliceDeviceService(
    private val deviceRepository: DeviceRepository,
    private val deviceActionRepository: DeviceActionRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun getUserDevices(userId: String): Result<List<Device>> {
        return Result.success(deviceRepository.getDevicesByUserId(userId))
    }

    suspend fun getDeviceById(userId: String, id: Int): Result<Device> {
        val device = deviceRepository.getDeviceById(id)
        if (device != null && device.userId == userId) {
            return Result.success(device)
        }
        return Result.failure(DeviceNotFoundException())
    }

    suspend fun getDevicesStatus(userId: String, devicesIds: List<String>): Result<List<DeviceStateDto>> {
        val devices = deviceRepository.getDevicesByIds(devicesIds.map(String::toInt))
        val devicesStatuses = devices.map { device ->
            if (device.userId != userId) {
                return Result.failure(UserNotFoundException())
            }
            val onOffCapabilityState = DeviceCapability.OnOffCapability.OnOffState(value = device.isOn, instance = CapabilityInstance.OnOffCapabilityInstance.ON_OFF)
            val onOffCapability = DeviceCapability.OnOffCapability(state = onOffCapabilityState)
            DeviceStateDto(device.id.toString(), capabilities = listOf(onOffCapability))
        }
        return Result.success(devicesStatuses)
    }

    suspend fun sendDevicesActions(
        userId: String,
        devicesStatuses: List<DeviceStateDto>
    ): List<DeviceChangeStateResult> {
        val results = mutableListOf<DeviceChangeStateResult>()
        val jobs = mutableListOf<Job>()
        val mutex = Mutex()
        devicesStatuses.forEach { deviceStatus ->
            val job = scope.launch {
                val result = sendDeviceActions(userId, deviceStateDto = deviceStatus)
                mutex.withLock {
                    results.add(result)
                }
            }
            jobs.add(job)
        }
        jobs.forEach { it.join() }
        return results
    }

    private suspend fun sendDeviceActions(userId: String, deviceStateDto: DeviceStateDto): DeviceChangeStateResult {
        val device = deviceRepository.getDeviceById(deviceStateDto.deviceId.toInt())
        val currentDeviceState = deviceRepository.getDeviceState(deviceStateDto.deviceId.toInt())
        if (device == null || device.userId != userId || currentDeviceState == null) {
            return DeviceChangeStateResult(
                id = deviceStateDto.deviceId,
                capabilities = deviceStateDto.capabilities.map {
                    CapabilityChangeResult(
                        state = CapabilityChangeResultState(
                            instance = it.state.instance,
                            actionResult = ActionResult(status = "ERROR", errorMessage = "UNKNOWN DEVICE")
                        ), type = "UNKNOWN TYPE"
                    )
                }
            )
        }
        var targetDeviceState: DeviceState = currentDeviceState
        val capabilities = deviceStateDto.capabilities.map { capability ->
            targetDeviceState = when (capability) {
                is DeviceCapability.OnOffCapability -> {
                    targetDeviceState.copy(isOn = capability.state.value)
                }

                is DeviceCapability.RangeCapability -> {
                    targetDeviceState.copy(temperature = capability.state.value)
                }
            }
            val capabilityType = capability.toCapabilityType()
            val state = CapabilityChangeResultState(instance = capability.state.instance, actionResult = ActionResult.SUCCESS_RESULT)
            CapabilityChangeResult(state = state, type = capabilityType)
        }
        deviceActionRepository.sendAction(
            deviceId = deviceStateDto.deviceId,
            action = targetDeviceState.toDeviceAction()
        )
        deviceRepository.updateDeviceState(deviceStateDto.deviceId.toInt(), targetDeviceState)
        return DeviceChangeStateResult(
            id = deviceStateDto.deviceId,
            capabilities = capabilities
        )
    }
}

private fun DeviceActionResult.toActionResult(): ActionResult {
    return if (status == Status.SUCCESS) {
        ActionResult(status = "DONE")
    } else {
        ActionResult(status = "ERROR", errorMessage = message)
    }
}