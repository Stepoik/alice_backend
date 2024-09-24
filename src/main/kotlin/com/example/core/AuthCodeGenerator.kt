package com.example.core

import kotlin.random.Random

object AuthCodeGenerator {
    fun generate(): String {
        return StringEncoder.encode(Random.nextInt().toString(), algorithm = EncodeAlgorithm.MD5)
    }
}