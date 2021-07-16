package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import zmq.socket.pubsub.Sub

object KrakenWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class Subscription(
        val name: String,
    )

    @Serializable
    data class KrakenWebsocketRequest(
        val event: String,
        val pair: List<String>,
        val subscription: Subscription,
    )

    private val channels = listOf(
        "trade",
        "book",
    )

    override fun buildRequest(symbols: List<String>): List<String> {
        val requests = channels.map {
            KrakenWebsocketRequest(
                "subscribe",
                symbols,
                Subscription(it),
            )
        }

        return requests.map { Json.encodeToString(it) }
    }

}