package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CoinbaseWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class CoinbaseWebsocketRequest(
        val type: String,
        val product_ids: List<String>,
        val channels: List<String>,
    )

    private val channels = listOf(
        "level2",
        "heartbeat",
        "ticker",
    )

    override fun buildRequest(symbols: List<String>): List<String> {
        val request = CoinbaseWebsocketRequest(
            "subscribe",
            symbols,
            channels
        )

        return listOf(Json.encodeToString(request))
    }

}