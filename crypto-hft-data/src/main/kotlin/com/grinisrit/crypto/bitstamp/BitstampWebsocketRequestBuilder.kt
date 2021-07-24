package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.BitstampPlatform
import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun BitstampPlatform.createBitstampRequests() =
    BitstampWebsocketRequestBuilder.buildRequest(symbols)

object BitstampWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class Data(
        val channel: String
    )

    @Serializable
    data class BitstampWebsocketRequest(
        val event: String,
        val data: Data,
    )

    private val channels = listOf(
        "detail_order_book",
        "live_trades",
    )

    override fun buildRequest(symbols: List<String>): List<String> {
        val requests = channels.flatMap { channel ->
            symbols.map { symbol ->
                BitstampWebsocketRequest(
                    "bts:subscribe",
                    Data("${channel}_${symbol}")
                )
            }
        }

        return requests.map { Json.encodeToString(it) }
    }

}