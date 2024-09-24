package com.example.core

class PasswordEncoder {
    companion object {
        fun encode(password: String): String {
            return StringEncoder.encode(string = password, algorithm = EncodeAlgorithm.SHA256)
        }
    }
}