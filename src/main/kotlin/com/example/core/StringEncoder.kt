package com.example.core

import java.security.MessageDigest

enum class EncodeAlgorithm(val value: String) {
    SHA256("SHA-256"),
    MD5("MD5")
}

class StringEncoder {
    companion object {
        fun encode(string: String, algorithm: EncodeAlgorithm): String {
            return MessageDigest.getInstance(algorithm.value).digest(string.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}