package com.example.api

import com.example.api.controllers.AliceController
import com.example.plugins.JWT_CONFIGURATION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin
import org.koin.ktor.ext.inject

private const val ALICE_API_VERSION = "v1.0"

fun Application.configureAliceRouting() {
    val controller by inject<AliceController>()
    routing {
        route("/$ALICE_API_VERSION") {
            head("/") {
                HttpStatusCode
                call.respond(HttpStatusCode.OK)
            }
            route("/user") {
                authenticate(JWT_CONFIGURATION) {
                    post("/unlink") {
                        controller.performUnlinkUser(call)
                    }
                    route("/devices") {
                        get {
                            controller.performGetUserDevices(call)
                        }
                        post("/query") {
                            controller.performUserDevicesQuery(call)
                        }
                        post("/action") {
                            controller.performChangeDeviceStateAction(call)
                        }
                    }
                }
            }
        }
    }
}