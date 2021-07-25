package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.MarketDataParser
import com.grinisrit.crypto.logger
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.zeromq.ZMQ
import java.lang.Exception
import java.time.Instant

open class SeveralRequestWebsocketClient
internal constructor(
    platform: Platform,
    private val requests: List<String>,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L
) : WebsocketClient(
    platform,
    backendReconnectTimeout,
    socketTimeoutMillis
) {
    private fun dataStringOf(data: String) =
        MarketDataParser.dataStringOf(platform.name, Instant.now(), data)

    override suspend fun DefaultClientWebSocketSession.receiveData() = flow {
        debugLog("connected successfully")

        for (request in requests) {
            debugLog("sending request:\n$request")
            send(Frame.Text(request))
        }

        for (frame in incoming) {
            frame as? Frame.Text ?: throw Exception("Unexpected response: $frame")
            emit(dataStringOf(frame.readText()))
        }

    }
}