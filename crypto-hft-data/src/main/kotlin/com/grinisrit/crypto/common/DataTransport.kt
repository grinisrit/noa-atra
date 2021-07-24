package com.grinisrit.crypto.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.time.Instant

object DataTransport {

    private const val internalDelimiter = "/**/"

    private val decoder = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

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

    fun <T> decodeJsonData(
        jsonData: String,
        serializer: KSerializer<T>
    ) = decoder.decodeFromString(serializer, jsonData)

    fun <T : PlatformData> fromDataString(
        dataString: String,
        serializer: KSerializer<T>
    ): MarketData<T> {
        val (_, receivingDateTimeString, jsonData) = dataString.split(internalDelimiter)
        return MarketData(
            Instant.parse(receivingDateTimeString),
            decodeJsonData(jsonData, serializer)
        )
    }

}