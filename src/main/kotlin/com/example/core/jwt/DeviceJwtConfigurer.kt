package com.example.core.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*

class DeviceJwtConfigurer(private val secret: String) {
    companion object {
        private const val CLAIM_DEVICE_ID = "device_id"
        private const val EXPIRES_IN = "expires_in"
        private const val ISSUER = "com.stepa.bread"
    }

    private val jwtAlgorithm = Algorithm.HMAC256(secret)

    fun generateToken(deviceId: String): String {
        return JWT.create().withSubject("Authentication")
            .withClaim(CLAIM_DEVICE_ID, deviceId)
            .withIssuer(ISSUER)
            .withClaim(EXPIRES_IN, Int.MAX_VALUE)
            .sign(jwtAlgorithm)
    }

    fun configureJwt(config: JWTAuthenticationProvider.Config) = with(config) {
        verifier(
            JWT
                .require(jwtAlgorithm)
                .withIssuer(ISSUER)
                .build()
        )
        validate { credential ->
            val deviceId = credential.payload.getClaim(CLAIM_DEVICE_ID).asString()
            if (credential.payload.issuer == ISSUER) JWTPrincipal(payload = credential.payload) else null
        }
    }
}