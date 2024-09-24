package com.example.core

import java.util.*

object UUIDGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}