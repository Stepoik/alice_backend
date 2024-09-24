package com.example.api

import com.example.api.dtos.*
import com.example.api.requests.UserRegistrationRequest
import com.example.api.responses.TokenResponse
import com.example.api.templates.authCss
import com.example.api.templates.authTemplate
import com.example.api.templates.registrationTemplate
import com.example.domain.models.CodeWithRedirectURI
import com.example.domain.services.AuthClientService
import com.example.plugins.UserAuthSession
import com.example.plugins.respondCss
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import org.koin.ktor.ext.inject

private const val CLIENT_ID = "client_id"
private const val CLIENT_SECRET = "client_secret"
private const val REDIRECT_URI = "redirect_uri"
private const val STATE = "state"

private const val BASE_REDIRECT_URI = "https://ya.ru"

fun Application.configureAuthRouting() {
    val service by inject<AuthClientService>()
    routing {
        post("/client/create") {
            val clientCredentials = call.receive<ClientCredentials>()
            service.createClient(clientCredentials)
        }

        get("/auth") {
            val clientId = call.request.queryParameters[CLIENT_ID] ?: run {
                call.respondHtml { clientIdIncorrectHtml() }
                return@get
            }

            val redirectUri = call.request.queryParameters[REDIRECT_URI] ?: BASE_REDIRECT_URI
            val state = call.request.queryParameters[STATE] ?: ""
            call.sessions.set(UserAuthSession(id = clientId, redirectUri = redirectUri, state = state))

            if (!service.isClientIdExist(clientId)) {
                call.respondHtml { clientIdIncorrectHtml() }
                return@get
            }
            call.respondHtml { authTemplate(clientId = clientId, redirectUrl = redirectUri, state = state) }
        }

        get("/registration") {
            val clientId = call.request.queryParameters[CLIENT_ID] ?: run {
                call.respondHtml { clientIdIncorrectHtml() }
                return@get
            }
            val state = call.request.queryParameters[STATE] ?: ""
            val redirectUri = call.request.queryParameters[REDIRECT_URI] ?: BASE_REDIRECT_URI
            call.sessions.set(UserAuthSession(id = clientId, redirectUri = redirectUri, state = state))

            if (!service.isClientIdExist(clientId)) {
                call.respondHtml { clientIdIncorrectHtml() }
                return@get
            }
            call.respondHtml { registrationTemplate(clientId = clientId, redirectUri = redirectUri, state = state) }
        }

        post("/registration") {
            val credentials = call.getUserRegistrationForm() ?: run { call.respond(Message("Bad request")); return@post }
            val clientId = call.request.queryParameters[CLIENT_ID]
                ?: run { call.respond(Message("Requires auth code")); return@post }
            val authCodeResult = service.getRegisterAuthCode(clientId = clientId, credentials)
            val session = call.sessions.get<UserAuthSession>()
            val redirectUri = session?.redirectUri ?: BASE_REDIRECT_URI
            val state = session?.state ?: ""

            call.respondAuthResult(authCodeResult, redirectUri = redirectUri, state = state)
        }

        post("/auth") {
            val credentials = call.getCredentials() ?: return@post
            val clientId = call.request.queryParameters[CLIENT_ID]
                ?: run { call.respond(Message("Requires auth code")); return@post }
            val authCodeResult = service.getAuthCode(clientId = clientId, credentials = credentials)
            val session = call.sessions.get<UserAuthSession>()
            val redirectUri = session?.redirectUri ?: BASE_REDIRECT_URI
            val state = session?.state ?: ""

            call.respondAuthResult(authCodeResult, redirectUri = redirectUri, state = state)
        }

        post("/token") {
            val params = call.receiveParameters()
            val secret = params[CLIENT_SECRET]
                ?: run { call.respond(Message("Requires client secret")); return@post }
            val clientId =
                params[CLIENT_ID] ?: run { call.respond(Message("Requires client id")); return@post }
            val code = params["code"]
                ?: run { call.respond(Message("Requires auth code")); return@post }

            val tokenResult = service.authorizeUser(clientId, secret, code)

            tokenResult.onSuccess { token ->
                call.respond(
                    TokenResponse(
                    accessToken = token,
                    refreshToken = token,
                    tokenType = "bearer",
                    expiresIn = Int.MAX_VALUE
                )
                )
            }.onFailure {
                call.respond(it.message ?: "error")
            }
        }

        get("/styles.css") {
            call.respondCss {
                authCss()
            }
        }
    }
}

private suspend fun ApplicationCall.respondAuthResult(result: Result<CodeWithRedirectURI>, state: String, redirectUri: String) {
    result.onSuccess { authCode ->
        respondRedirect("$redirectUri?code=${authCode.code}&state=$state", permanent = true)
    }.onFailure { error ->
        request.headers["Referer"]?.let {
            respondRedirect(it, permanent = true)
        } ?: respondRedirect("/auth")
    }
}

private suspend fun ApplicationCall.getUserRegistrationForm(): UserRegistrationRequest? {
    val params = this.receiveParameters()
    val login = params["login"]
    val password = params["password"]
    val username = params["name"]
    if (login == null || password == null || username == null) {
        return null
    }
    return UserRegistrationRequest(
        login = login,
        password = password,
        username = username
    )
}

private suspend fun ApplicationCall.getCredentials(): UserCredentials? {
    val params = this.receiveParameters()
    if (!(params.contains("login") && params.contains("password"))) {
        return null
    }

    return UserCredentials(params["login"] ?: "", params["password"] ?: "")
}

private fun HTML.clientIdIncorrectHtml() = body {
    h1 { text("Клиент не существует") }
}