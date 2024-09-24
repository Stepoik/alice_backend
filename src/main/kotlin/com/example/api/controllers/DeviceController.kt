package com.example.api.controllers

import com.example.api.requests.BindDeviceRequest
import com.example.api.requests.DeviceAuthRequest
import com.example.api.requests.DeviceCreateRequest
import com.example.api.responses.CheckTokenResponse
import com.example.api.responses.DeviceAuthResponse
import com.example.api.responses.DeviceCreateResponse
import com.example.api.responses.ResponseStatus
import com.example.core.jwt.UserPrincipal
import com.example.domain.models.Device
import com.example.domain.services.DeviceService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class DeviceController(
    private val deviceService: DeviceService
) {
    suspend fun performCreateDevice(call: ApplicationCall) {
        val device = call.receive<DeviceCreateRequest>()
        val result = deviceService.addDevice(device)
        result.onSuccess {
            call.respond(DeviceCreateResponse(status = ResponseStatus.SUCCESS, secret = it?.secret, id = it?.id?.toString()))
        }.onFailure {
            call.respond(DeviceCreateResponse(status = ResponseStatus.FAILURE))
        }
    }

    suspend fun authenticateDevice(call: ApplicationCall) {
        val body = call.receive<DeviceAuthRequest>()
        val authResult = deviceService.authenticateDevice(deviceId = body.deviceId, secret = body.secret)
        authResult.onSuccess {
            call.respond(DeviceAuthResponse(status = ResponseStatus.SUCCESS, token = it))
        }.onFailure {
            call.respond(DeviceAuthResponse(status = ResponseStatus.FAILURE, token = null))
        }
    }

    suspend fun performCheckToken(call: ApplicationCall) {
        call.respond(CheckTokenResponse(ok = true, error = ""))
    }

    suspend fun performACLCheck(call: ApplicationCall) {
        call.respond(CheckTokenResponse(ok = true, error = ""))
    }

    suspend fun performBindDevice(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()!!
        val body = call.receive<BindDeviceRequest>()
        val result = deviceService.bindDeviceToUser(userId = principal.userId, deviceId = body.deviceId)
        result.onSuccess {
            call.respond(mapOf("status" to "success"))
        }.onFailure {
            call.respond(mapOf("status" to "fail"))
        }
    }
}