package com.grinisrit.crypto.finery

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class AuthContent(
    val nonce: Long,
    val timestamp: Long,
)

fun getTimeNano(): Long = with(Instant.now()) {
    val nanoMult = 1000000
    return toEpochMilli() * nanoMult + nano % nanoMult
}


fun getAuthContent(): AuthContent =
    AuthContent(
        getTimeNano(),
        Instant.now().toEpochMilli()
    )

@Serializable
data class Auth(
    val event: String,
    val content: String,
    val key: String,
    val signature: String
)