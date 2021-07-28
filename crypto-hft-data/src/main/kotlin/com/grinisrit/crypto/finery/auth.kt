package com.grinisrit.crypto.finery

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.codec.binary.*
import org.apache.commons.codec.digest.*
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


fun getAuthContentNano(): AuthContent =
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

fun getAuth(key: String, secret: String): Auth {
    val content = getAuthContentNano()
    val contentEncoded = Json { }.encodeToString(content)
    val signature = Base64.encodeBase64(
        HmacUtils(HmacAlgorithms.HMAC_SHA_384, secret).hmac(contentEncoded)
    ).decodeToString()

    return Auth(
        "auth",
        contentEncoded,
        key,
        signature
    )
}

fun getAuthRequest(key: String, secret: String): String {
    return Json {  }.encodeToString(getAuth(key, secret))
}
