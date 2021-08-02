package com.grinisrit.crypto.finery

import com.grinisrit.crypto.FineryPlatform
import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun FineryPlatform.createFineryRequest() =
    FineryWebsocketRequestBuilder.buildRequest(symbols)


object FineryWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class FineryWebsocketRequest(
        val event: String,
        val feed: String,
        val feedId: Long,
    )


    override fun buildRequest(symbols: List<String>): List<String> {

        return symbols.map {
            Json.encodeToString(
                FineryWebsocketRequest(
                    "bind",
                    "B",
                    symbolToFeedId[it]!!
                )
            )
        }

    }

}