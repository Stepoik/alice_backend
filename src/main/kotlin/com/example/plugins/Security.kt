package com.example.plugins

import com.example.core.jwt.DeviceJwtConfigurer
import com.example.core.jwt.JwtConfigurer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.getKoin

fun Application.configureSecurity() {
    val jwtConfigurer = getKoin().get<JwtConfigurer>()
    val deviceJwtConfigurer = getKoin().get<DeviceJwtConfigurer>()
    authentication {
        jwt(JWT_CONFIGURATION) {
            jwtConfigurer.configureJwt(this)
        }

        jwt(DEVICE_JWT_CONFIGURATION) {
            deviceJwtConfigurer.configureJwt(this)
        }
    }
}

const val JWT_CONFIGURATION = "jwt"
const val DEVICE_JWT_CONFIGURATION = "jwt_device"
