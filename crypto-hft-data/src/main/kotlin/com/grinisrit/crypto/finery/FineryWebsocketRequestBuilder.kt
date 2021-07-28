package com.grinisrit.crypto.finery

import com.grinisrit.crypto.FineryPlatform
import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun FineryPlatform.createFineryRequest() =
    FineryWebsocketRequestBuilder.buildRequest(symbols).first()


object FineryWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class FineryWebsocketRequest(
        val event: String,
        val feed: String,
        val feedId: Long,
    )

    private val channels = listOf(
        "B"
    )

    override fun buildRequest(symbols: List<String>): List<String> {

        // TODO()
        val request = FineryWebsocketRequest(
            "bind",
            "B",
            4955410050
        )

        return listOf(Json.encodeToString(request))
    }

}