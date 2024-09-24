package com.example.plugins

import io.ktor.server.application.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import org.koin.ktor.ext.getKoin

fun Application.configureEnvMonitoring() {
    environment.monitor.subscribe(ApplicationStopping) {
        closeRedis()
    }
}

private fun Application.closeRedis() {
    val redisConnection: StatefulRedisConnection<String, String> = getKoin().get()
    val redisClient: RedisClient = getKoin().get()
    redisConnection.close()
    redisClient.close()
}