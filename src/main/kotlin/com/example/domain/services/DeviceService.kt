package com.example.domain.services

import com.example.api.requests.DeviceCreateRequest
import com.example.core.UUIDGenerator
import com.example.core.jwt.DeviceJwtConfigurer
import com.example.domain.exceptions.DeviceNotFoundException
import com.example.domain.exceptions.UserNotFoundException
import com.example.domain.models.Device
import com.example.domain.repositories.DeviceRepository
import com.example.domain.repositories.UserRepository

class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val userRepository: UserRepository,
    private val deviceJwtGenerator: DeviceJwtConfigurer
) {

    suspend fun addDevice(deviceRequest: DeviceCreateRequest) : Result<Device?> {
        val secret = UUIDGenerator.generate()
        val device = deviceRequest.toDevice(secret)
        return runCatching {
            deviceRepository.addDevice(device)
        }.map {
            deviceRepository.getDeviceById(device.id)
        }
    }

    suspend fun authenticateDevice(deviceId: String, secret: String): Result<String> {
        val device = deviceRepository.getDeviceById(deviceId.toInt()) ?: return Result.failure(DeviceNotFoundException())
        if (device.secret != secret) return Result.failure(DeviceNotFoundException())
        val token = deviceJwtGenerator.generateToken(device.id.toString())
        return Result.success(token)
    }

    suspend fun bindDeviceToUser(userId: String, deviceId: Int): Result<String> {
        val user = userRepository.getUserById(userId) ?: return Result.failure(UserNotFoundException())
        val device = deviceRepository.getDeviceById(deviceId) ?: return Result.failure(DeviceNotFoundException())
        deviceRepository.setUser(userId = user.id, deviceId = device.id)
        return Result.success("Success")
    }
}

private fun DeviceCreateRequest.toDevice(secret: String) = Device(
    id = id,
    name = name,
    description = description,
    type = type,
    userId = null,
    isOn = false,
    temperature = 20f,
    secret = secret
)