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

    // TODO(
    private val symbolToFeedId = mapOf(
        "BTC-USD" to 4955410050,
        "ETH-USD" to 4955415173,
        "ETH-BTC" to 3895304837,
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