package com.example.api.controllers

import com.example.api.dtos.Message
import com.example.api.requests.ChangeDeviceStateActionRequest
import com.example.api.requests.UserDevicesStatusRequest
import com.example.api.responses.DevicesChangeStatesPayload
import com.example.api.responses.DevicesChangeStatesResponse
import com.example.api.responses.DevicesResponse
import com.example.api.responses.DevicesStatusResponse
import com.example.core.jwt.UserPrincipal
import com.example.domain.services.AliceDeviceService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AliceController(
    private val aliceDeviceService: AliceDeviceService
) {
    suspend fun performUnlinkUser(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()!!
        val requestId = call.request.headers["X-Request-Id"]
        call.respond(mapOf("request_id" to requestId))
    }

    suspend fun performGetUserDevices(call: ApplicationCall) {
        val requestId = call.request.headers["X-Request-Id"] ?: "1"
        val principal = call.principal<UserPrincipal>()!!
        val devicesResult = aliceDeviceService.getUserDevices(principal.userId)
        devicesResult.onSuccess { devices ->
            val response = DevicesResponse(requestId = requestId, userId = principal.userId, devices = devices)
            call.respond(response)
        }.onFailure {
            call.respond(Message(it.message ?: "error"))
        }
    }

    suspend fun performUserDevicesQuery(call: ApplicationCall) {
        val requestId = call.request.headers["X-Request-Id"] ?: "1"
        val principal = call.principal<UserPrincipal>()!!
        val requestBody = call.receive<UserDevicesStatusRequest>()
        val devicesStatusResult =
            aliceDeviceService.getDevicesStatus(userId = principal.userId, devicesIds = requestBody.devices.map { it.id })
        devicesStatusResult.onSuccess { devices ->
            val response = DevicesStatusResponse(requestId = requestId, devices = devices)
            call.respond(response)
        }.onFailure {
            call.respond(Message(it.message ?: "error"))
        }
    }

    suspend fun performChangeDeviceStateAction(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()!!
        val requestId = call.request.headers["X-Request-Id"] ?: "1"
        val requestBody = call.receive<ChangeDeviceStateActionRequest>()
        val devicesResults = aliceDeviceService.sendDevicesActions(userId = principal.userId, devicesStatuses = requestBody.payload.devices)
        val response = DevicesChangeStatesResponse(
            requestId = requestId,
            payload = DevicesChangeStatesPayload(devices = devicesResults)
        )
        call.respond(response)
    }
}