package com.grinisrit.crypto.common

import com.beust.klaxon.Klaxon
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import java.time.Instant

// TODO() mb refactor
object DataTransport {

    const val internalDateTimeFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'"
    const val internalDelimiter = "/**/"
    //   const val internalDataFormat = "$internalDelimiter(.+)$internalDelimiter(.+)$internalDelimiter(.+)$internalDelimiter"

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

    // TODO() make this function better way
    inline fun <reified T : ChannelData> fromDataString(dataString: String): DataTime<T> {
        val (_, receivingDateTimeString, dataJSON) = dataString.split(internalDelimiter)
        return DataTime(
            Instant.parse(receivingDateTimeString),
            Klaxon().parse<T>(dataJSON)!! // TODO() refactor
        )
    }

    fun <T : ChannelData> fromDataString(
        dataString: String,
        serializer: JsonContentPolymorphicSerializer<T>
    ): DataTime<T> {
        val (_, receivingDateTimeString, dataJSON) = dataString.split(internalDelimiter)
        return DataTime(
            Instant.parse(receivingDateTimeString),
            Json.decodeFromString(serializer, dataJSON)
        )
    }

    fun <T : ChannelData> fromDataString(
        dataString: String,
        parser: CustomJsonParser<T>,
    ): DataTime<T> {
        val (_, receivingDateTimeString, dataJSON) = dataString.split(internalDelimiter)
        return DataTime(
            Instant.parse(receivingDateTimeString),
            parser.parse(dataJSON)
        )
    }
}