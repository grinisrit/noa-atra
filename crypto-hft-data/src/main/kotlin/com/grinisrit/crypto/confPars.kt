package com.grinisrit.crypto

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable

@Serializable
data class MongoDB(
    val address: String,
    val port: String,
    val status: String,
)
@Serializable
data class ConfYAMl(
    val version: String,
    val mongodb: MongoDB,
    val platforms: Platforms,
)

fun parseConf(input: String): ConfYAMl {
    return Yaml.default.decodeFromString(ConfYAMl.serializer(), input)
}
