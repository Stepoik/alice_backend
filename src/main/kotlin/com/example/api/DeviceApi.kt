package com.example.api

import com.example.api.controllers.DeviceController
import com.example.plugins.DEVICE_JWT_CONFIGURATION
import com.example.plugins.JWT_CONFIGURATION
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureDeviceRouting() {
    val controller by inject<DeviceController>()
    routing {
        route("/device") {
            authenticate(JWT_CONFIGURATION) {
                post {
                    controller.performCreateDevice(call)
                }

                post("/bind") {
                    controller.performBindDevice(call)
                }
            }
            route("/auth") {
                post {
                    controller.authenticateDevice(call)
                }
                authenticate(DEVICE_JWT_CONFIGURATION) {
                    post("/check_token") {
                        controller.performCheckToken(call)
                    }
                    post("/acl") {
                        controller.performACLCheck(call)
                    }
                }
            }
        }
    }
}