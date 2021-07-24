package com.grinisrit.crypto

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable

@Serializable
data class MongoDBConfig(
    val address: String,
    val status: String,
) {
    val isOn: Boolean
        get() = status == "on"
}

@Serializable
data class ZeroMQConfig(
    val address: String,
)

@Serializable
data class ConfYAMl(
    val version: String,
    val zeromq: ZeroMQConfig,
    val mongodb: MongoDBConfig,
    val platforms: Platforms,
)

fun parseConf(input: String): ConfYAMl {
    return Yaml.default.decodeFromString(ConfYAMl.serializer(), input)
}
