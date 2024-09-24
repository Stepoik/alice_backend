package com.example.di

import com.example.Configuration
import com.example.api.controllers.AliceController
import com.example.api.controllers.DeviceController
import com.example.core.jwt.DeviceJwtConfigurer
import com.example.domain.services.AuthClientService
import com.example.core.jwt.JwtConfigurer
import com.example.data.DatabaseInitial
import com.example.data.repositories.*
import com.example.domain.repositories.*
import com.example.domain.services.AliceDeviceService
import com.example.domain.services.DeviceService
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import org.eclipse.paho.client.mqttv3.MqttClient
import org.jetbrains.exposed.sql.Database
import org.koin.core.scope.Scope
import org.koin.dsl.module

@OptIn(ExperimentalLettuceCoroutinesApi::class)
val appModule = module {
    single { RedisClient.create(Configuration.REDIS_URL) }
    single { redisConnection(get()) }
    single { redisCoroutines(get()) }
    single { database() }
    single<MqttClient> { provideMqttClient(mqttUri = Configuration.MQTT_HOST) }
    single<UserRepository>(createdAtStart = true) { UserRepositoryImpl(get()) }
    // TODO: Может криво работать потому что авторизация работает через этот же сервис(желательно выделить в отдельный микросервис)
    single<DeviceActionRepository> {
        DeviceActionRepositoryImpl(
            mqttClient = get(),
            serverToken = Configuration.MQTT_TOKEN,
            serverUserName = Configuration.MQTT_USERNAME
        )
    }
    single<DeviceRepository>(createdAtStart = true) { DeviceRepositoryImpl(get()) }
    single<AuthCodeRepository>(createdAtStart = true) { AuthCodeRepositoryImpl(get()) }
    single<AuthClientRepository>(createdAtStart = true) { AuthClientRepositoryImpl(get()) }
    single { JwtConfigurer(Configuration.JWT_SECRET) }
    single { DeviceJwtConfigurer(Configuration.DEVICE_JWT_SECRET) }

    single { AliceDeviceService(get(), get()) }
    single { AuthClientService(get(), get(), get(), get()) }
    single { DeviceService(get(), get(), get()) }

    single { DeviceController(get()) }
    single { AliceController(get()) }
}

private fun Scope.redisConnection(redisClient: RedisClient) = redisClient.connect()

@OptIn(ExperimentalLettuceCoroutinesApi::class)
private fun Scope.redisCoroutines(redisConnection: StatefulRedisConnection<String, String>): RedisCoroutinesCommands<String, String> {
    return redisConnection.coroutines()
}

private fun Scope.database() = Database.connect(
    url = Configuration.DATABASE_URL,
    driver = Configuration.DATABASE_DRIVER,
    user = Configuration.DATABASE_USERNAME,
    password = Configuration.DATABASE_PASSWORD
).apply {
    DatabaseInitial.initDatabase(this)
}