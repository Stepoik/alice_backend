package com.example.data.repositories

import com.example.domain.models.AuthCode
import com.example.domain.repositories.AuthCodeRepository
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.SetArgs
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthCodeRepositoryImpl @OptIn(ExperimentalLettuceCoroutinesApi::class) constructor(
    private val redisClient: RedisCoroutinesCommands<String, String>
) : AuthCodeRepository {

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun add(authCode: AuthCode): Unit = withContext(Dispatchers.IO) {
        redisClient.set(
            AuthCodeScheme.key(clientId = authCode.clientId, code = authCode.code),
            authCode.userId,
            setArgs = CODE_ARGS
        )
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun get(clientId: String, code: String): AuthCode? = withContext(Dispatchers.IO) {
        val userId = redisClient.get(AuthCodeScheme.key(clientId = clientId, code = code))
        return@withContext userId?.let { AuthCode(clientId = clientId, code = code, userId = userId) }
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun remove(clientId: String, code: String) {
        redisClient.del(AuthCodeScheme.key(clientId = clientId, code = code))
    }

    companion object {
        private val CODE_ARGS = SetArgs().px(60000)
    }
}

object AuthCodeScheme {
    fun key(clientId: String, code: String): String {
        return "codes:${clientId}:${code}"
    }
}