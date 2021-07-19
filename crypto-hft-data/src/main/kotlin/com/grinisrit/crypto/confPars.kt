package com.grinisrit.crypto

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable

@Serializable
data class MongoDB(
    val address: String,
    val status: String,
) {
    val isOn: Boolean
        get() = status == "on"
}

@Serializable
data class ZeroMQ(
    val address: String,
)

@Serializable
data class ConfYAMl(
    val version: String,
    val zeromq: ZeroMQ,
    val mongodb: MongoDB,
    val platforms: Platforms,
)

fun parseConf(input: String): ConfYAMl {
    return Yaml.default.decodeFromString(ConfYAMl.serializer(), input)
}
