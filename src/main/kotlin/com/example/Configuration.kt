package com.example

import java.util.*

object Configuration {
    private val properties = Properties()

    init {
        val classLoader = Thread.currentThread().contextClassLoader
        classLoader.getResourceAsStream("application.properties").use {
            properties.load(it)
        }
    }

    private const val DATABASE_URL_PROPERTY_NAME = "database.url"
    private const val DATABASE_USERNAME_PROPERTY_NAME = "database.username"
    private const val DATABASE_PASSWORD_PROPERTY_NAME = "database.password"
    private const val DATABASE_DRIVER_PROPERTY_NAME = "database.driver"

    private const val REDIS_URL_PROPERTY_NAME = "redis.url"

    private const val JWT_SECRET_PROPERTY_NAME = "jwt.secret"
    private const val JWT_DEVICE_SECRET_PROPERTY_NAME = "jwt.secret.device"

    private const val MQTT_HOST_PROPERTY_NAME = "mqtt.host"
    private const val MQTT_TOKEN_PROPERTY_NAME = "mqtt.token"
    private const val MQTT_USERNAME_PROPERTY_NAME = "mqtt.username"

    val DATABASE_URL: String = properties.getProperty(DATABASE_URL_PROPERTY_NAME)
    val DATABASE_USERNAME: String = properties.getProperty(DATABASE_USERNAME_PROPERTY_NAME)
    val DATABASE_PASSWORD: String = properties.getProperty(DATABASE_PASSWORD_PROPERTY_NAME)
    val DATABASE_DRIVER: String = properties.getProperty(DATABASE_DRIVER_PROPERTY_NAME)

    val REDIS_URL: String = properties.getProperty(REDIS_URL_PROPERTY_NAME)

    val JWT_SECRET: String = properties.getProperty(JWT_SECRET_PROPERTY_NAME)
    val DEVICE_JWT_SECRET: String = properties.getProperty(JWT_DEVICE_SECRET_PROPERTY_NAME)

    val MQTT_HOST: String = properties.getProperty(MQTT_HOST_PROPERTY_NAME)
    val MQTT_TOKEN: String = properties.getProperty(MQTT_TOKEN_PROPERTY_NAME)
    val MQTT_USERNAME: String = properties.getProperty(MQTT_USERNAME_PROPERTY_NAME)
}