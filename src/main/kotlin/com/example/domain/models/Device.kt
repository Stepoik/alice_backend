package com.example.domain.models

data class Device(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    val userId: String?,
    val isOn: Boolean,
    val temperature: Float,
    val secret: String
)
