package com.example.domain.repositories

import com.example.domain.models.Device
import com.example.domain.models.DeviceState


interface DeviceRepository {

    suspend fun getDeviceState(id: Int): DeviceState?

    suspend fun getDevicesByUserId(userId: String): List<Device>

    suspend fun getDevicesByIds(ids: List<Int>): List<Device>

    suspend fun getDeviceById(id: Int): Device?

    suspend fun addDevice(device: Device)

    suspend fun removeDevice(id: Int)

    suspend fun updateDeviceState(id: Int, deviceState: DeviceState)

    suspend fun setUser(userId: String, deviceId: Int)
}