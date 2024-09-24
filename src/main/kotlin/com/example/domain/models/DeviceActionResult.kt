package com.example.domain.models

data class DeviceActionResult(
    val status: Status,
    val message: String? = null
)

enum class Status {
    SUCCESS,
    FAILURE;

    fun isSuccess() = this == SUCCESS
}