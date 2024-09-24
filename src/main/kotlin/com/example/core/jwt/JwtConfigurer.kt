package com.example.core.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*

class JwtConfigurer(private val secret: String) {
    companion object {
        private const val CLAIM_USERID = "user_id"
        private const val CLAIM_USERNAME = "user_name"
        private const val EXPIRES_IN = "expires_in"
        private const val JWT_AUDIENCE = "user"
        private const val JWT_REALM = "me_and_me"
        private const val ISSUER = "com.stepa.bread"
    }

    private val jwtAlgorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: String, userName: String): String {
        return JWT.create().withSubject("Authentication")
            .withClaim(CLAIM_USERID, userId)
            .withClaim(CLAIM_USERNAME, userName)
            .withIssuer(ISSUER)
            .withClaim(EXPIRES_IN, Int.MAX_VALUE)
            .sign(jwtAlgorithm)
    }

    fun configureJwt(config: JWTAuthenticationProvider.Config) = with(config) {
        realm = JWT_REALM
        verifier(
            JWT
                .require(jwtAlgorithm)
                .withIssuer(ISSUER)
                .build()
        )
        validate { credential ->
            val userId = credential.payload.getClaim(CLAIM_USERID).asString()
            val userName = credential.payload.getClaim(CLAIM_USERNAME).asString()
            if (credential.payload.issuer == ISSUER) UserPrincipal(userId = userId, userName = userName) else null
        }
    }
}