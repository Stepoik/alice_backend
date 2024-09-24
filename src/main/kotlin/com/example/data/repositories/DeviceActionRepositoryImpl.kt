package com.example.data.repositories

import com.example.domain.models.Device
import com.example.domain.models.DeviceAction
import com.example.domain.models.DeviceActionResult
import com.example.domain.models.Status
import com.example.domain.repositories.DeviceActionRepository
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.*
import org.slf4j.LoggerFactory

class DeviceActionRepositoryImpl(
    private val mqttClient: MqttClient,
    private val serverToken: String,
    private val serverUserName: String
) : DeviceActionRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.isCleanSession = true
        options.password = serverToken.toCharArray()
        options.userName = serverUserName
        mqttClient.connect(options)
    }

    override suspend fun sendAction(deviceId: String, action: DeviceAction): DeviceActionResult = withContext(Dispatchers.IO) {
        val message = getActionMessage(action)
        mqttClient.publish("device/$deviceId", message)
        return@withContext DeviceActionResult(status = Status.SUCCESS)
    }

    private fun getActionMessage(action: DeviceAction): MqttMessage {
        return MqttMessage("${ action.isOn.compareTo(false) }:${action.temperature}".encodeToByteArray())
    }
}