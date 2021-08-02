package com.grinisrit.crypto

import com.charleskorn.kaml.Yaml
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import kotlinx.serialization.Serializable
import java.io.File

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

fun loadConf(args: Array<String>): ConfYAMl {
    val cliParser = ArgParser("data")
    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()
    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"
    return parseConf(File(configPath).readText())
}
