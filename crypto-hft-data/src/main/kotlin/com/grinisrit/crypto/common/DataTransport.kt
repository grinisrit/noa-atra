package com.grinisrit.crypto.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import java.time.Instant

// TODO() mb refactor
object DataTransport {

    private const val internalDelimiter = "/**/"

    fun dataStringOf(platformName: String, receivingDateTime: Instant, dataJSON: String): String {
        return buildString {
            append(platformName)
            append(internalDelimiter)
            append(receivingDateTime.toString())
            append(internalDelimiter)
            append(dataJSON)
        }
    }

    fun getPlatformName(dataString: String): String {
        return dataString.split(internalDelimiter).first()
    }

    fun <T : ChannelData> decodeJsonData(
        jsonData: String,
        serializer: KSerializer<T>
    ) = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }.decodeFromString(serializer, jsonData)

    fun <T : ChannelData> fromDataString(
        dataString: String,
        serializer: KSerializer<T>
    ): DataTime<T> {
        val (_, receivingDateTimeString, jsonData) = dataString.split(internalDelimiter)
        return DataTime(
            Instant.parse(receivingDateTimeString),
            decodeJsonData(jsonData, serializer)
        )
    }

}