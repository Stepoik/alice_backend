package com.example.core

object UserIdGenerator {
    fun generate(): String {
        return UUIDGenerator.generate()
    }
}