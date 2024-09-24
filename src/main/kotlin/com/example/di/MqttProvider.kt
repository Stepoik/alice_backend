package com.example.di

import org.eclipse.paho.client.mqttv3.MqttClient

fun provideMqttClient(mqttUri: String): MqttClient {
    val client = MqttClient(mqttUri, "server")
    return client
}