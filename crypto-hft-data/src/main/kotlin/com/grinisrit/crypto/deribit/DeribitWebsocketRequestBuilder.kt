package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.DeribitPlatform
import com.grinisrit.crypto.common.RequestBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun DeribitPlatform.createDeribitRequest() =
    DeribitWebsocketRequestBuilder.buildRequest(symbols).first()

object DeribitWebsocketRequestBuilder : RequestBuilder {

    @Serializable
    data class Parameters(
        val channels: List<String>
    )

    @Serializable
    data class DeribitWebsocketRequest(
        val jsonrpc: String,
        val method: String,
        val id: Int,
        val params: Parameters,
    )

    private val channels = listOf(
        "book" to "none.20.100ms",
        "trades" to "raw",
    )

    override fun buildRequest(symbols: List<String>): List<String> {
        val request = DeribitWebsocketRequest(
            "2.0",
            "public/subscribe",
            42,
            Parameters(
                channels.flatMap { channel ->
                    symbols.map { symbol ->
                        "${channel.first}.$symbol.${channel.second}"
                    }
                }
            )
        )

        return listOf(Json.encodeToString(request))
    }

}