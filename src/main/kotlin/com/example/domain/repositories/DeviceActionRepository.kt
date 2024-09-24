package com.example.domain.repositories

import com.example.domain.models.DeviceAction
import com.example.domain.models.DeviceActionResult

interface DeviceActionRepository {
    suspend fun sendAction(deviceId: String, action: DeviceAction): DeviceActionResult
}