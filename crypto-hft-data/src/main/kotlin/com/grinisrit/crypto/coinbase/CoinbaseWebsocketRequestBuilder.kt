package com.grinisrit.crypto.coinbase


import com.grinisrit.crypto.CoinbasePlatform
import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun CoinbasePlatform.createCoinbaseRequest() =
    CoinbaseWebsocketRequestBuilder.buildRequest(symbols).first()


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
        "matches",
        //   "ticker",
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