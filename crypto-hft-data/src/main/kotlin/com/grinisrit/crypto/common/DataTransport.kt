package com.grinisrit.crypto.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import java.time.Instant

// TODO() mb refactor
object DataTransport {

    private const val internalDelimiter = "/**/"

    data class DataTime<T : ChannelData>(
        val receiving_datetime: Instant,
        val data: T,
    )

    fun dataStringOf(platformName: String, receivingDateTime: Instant, dataJSON: String): String {
        return buildString {
            append(platformName)
            append(internalDelimiter)
            append(receivingDateTime.toString())
            append(internalDelimiter)
            append(dataJSON)
            append(internalDelimiter)
        }
    }

    fun getPlatformName(dataString: String): String {
        return dataString.split(internalDelimiter).first()
    }

    fun <T : ChannelData> fromDataString(
        dataString: String,
        serializer: JsonContentPolymorphicSerializer<T>
    ): DataTime<T> {
        val (_, receivingDateTimeString, dataJSON) = dataString.split(internalDelimiter)
        return DataTime(
            Instant.parse(receivingDateTimeString),
            Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            }.decodeFromString(serializer, dataJSON)
        )
    }

}