package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserAuthSession>("auth_session")
    }
}

data class UserAuthSession(val id: String, val redirectUri: String, val state: String)