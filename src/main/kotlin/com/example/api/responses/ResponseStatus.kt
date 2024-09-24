package com.example.api.responses

import kotlinx.serialization.Serializable

@Serializable
enum class ResponseStatus {
    SUCCESS,
    FAILURE
}