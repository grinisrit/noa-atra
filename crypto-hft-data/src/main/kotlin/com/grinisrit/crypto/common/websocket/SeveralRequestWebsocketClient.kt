package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.MarketDataParser
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.time.Instant

open class SeveralRequestWebsocketClient
internal constructor(
    platform: Platform,
    private val requests: List<String>,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L,
    aliveBound: Int = 10000
) : WebsocketClient(
    platform,
    backendReconnectTimeout,
    socketTimeoutMillis,
    aliveBound
) {
    private fun dataStringOf(data: String) =
        MarketDataParser.dataStringOf(platform.name, Instant.now(), data)

    override suspend fun DefaultClientWebSocketSession.receiveData() = flow {
        debugLog("connected successfully")

        for (request in requests) {
            debugLog("sending request:\n$request")
            send(Frame.Text(request))
        }

        var messagesReceived = 0

        for (frame in incoming) {
            frame as? Frame.Text ?: throw Exception("Unexpected response: $frame")
            emit(dataStringOf(frame.readText()))

            messagesReceived += 1
            if (messagesReceived.mod(aliveBound) == 0)
                debugLog("connection alive, received $messagesReceived messages")
        }

    }
}