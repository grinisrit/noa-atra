package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.RawMarketDataFlow
import com.grinisrit.crypto.logger

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.time.Instant


abstract class WebsocketClient(
    protected val platform: Platform,
    private val reconnectTimeoutMillis: Long = 5000L,
    private val socketTimeoutMillis: Long = 2000L
) {

    private var lastConnectionTimeMilli: Long = 0L

    fun getFlow(): RawMarketDataFlow = flow {
        while (true) {
            try {
                val instant = Instant.now()
                val currentTimeMilli = instant.toEpochMilli()

                val timeFromLastConnectionMilli = currentTimeMilli - lastConnectionTimeMilli

                delay(reconnectTimeoutMillis - timeFromLastConnectionMilli)

                lastConnectionTimeMilli = Instant.now().toEpochMilli()

                rawMarketDataFlow().collect {
                    emit(it)
                }

            } catch (e: Throwable) {
                logger.error(e) { "Failed to connect to ${platform.name}" }
            }
        }
    }

    protected abstract suspend fun DefaultClientWebSocketSession.receiveData(): RawMarketDataFlow

    private suspend fun rawMarketDataFlow() = flow {
        val client = HttpClient(CIO) {
            install(WebSockets)
            install(HttpTimeout) {
                socketTimeoutMillis = this@WebsocketClient.socketTimeoutMillis
            }
        }

        client.wss(urlString = platform.websocketAddress) {
            this.receiveData().collect { emit(it) }
        }

    }
}

