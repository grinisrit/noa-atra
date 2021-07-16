package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object BinanceWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class BinanceWebsocketRequest(
        val method: String,
        val params: List<String>,
        val id: Int,
    )

    private val channels = listOf(
        "@trade",
        "@depth@100ms",
    )

    override fun buildRequest(symbols: List<String>): List<String> {
        val request = BinanceWebsocketRequest(
            "SUBSCRIBE",
            channels.flatMap { channel ->
                symbols.map { symbol ->
                    symbol + channel
                }
            },
            1
        )

        return listOf(Json.encodeToString(request))
    }

}