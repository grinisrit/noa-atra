package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.KrakenPlatform
import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun KrakenPlatform.createKrakenRequests() =
    KrakenWebsocketRequestBuilder.buildRequest(symbols)

object KrakenWebsocketRequestBuilder : RequestBuilder {

    interface Subscription {
        val name: String
    }

    @Serializable
    data class BookSubscription(val depth: Int, override val name: String = "book") : Subscription

    @Serializable
    data class TradeSubscription(override val name: String = "trade") : Subscription

    @Serializable
    data class KrakenWebsocketBookRequest(
        override val event: String,
        override val pair: List<String>,
        override val subscription: BookSubscription,
    ): KrakenWebsocketRequest

    @Serializable
    data class KrakenWebsocketTradeRequest(
        override val event: String,
        override val pair: List<String>,
        override val subscription: TradeSubscription,
    ) : KrakenWebsocketRequest

    interface KrakenWebsocketRequest {
        val event: String
        val pair: List<String>
        val subscription: Subscription
    }

    override fun buildRequest(symbols: List<String>): List<String> {

        val encoder = Json {
            encodeDefaults = true
        }

        return listOf(
            encoder.encodeToString(
                KrakenWebsocketBookRequest(
                    "subscribe",
                    symbols,
                    BookSubscription(1000),
                )
            ),
            encoder.encodeToString(
                KrakenWebsocketTradeRequest(
                    "subscribe",
                    symbols,
                    TradeSubscription(),
                )
            )
        )
    }

}