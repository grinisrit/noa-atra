package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.JsonStringDataFlow
import com.grinisrit.crypto.commonLogger

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import java.time.Instant


abstract class WebsocketClient(
    protected val platform: Platform,
    private val reconnectTimeoutMillis: Long = 5000L,
    private val socketTimeoutMillis: Long = 2000L,
    protected val aliveBound: Int = 10000,
) {

    protected val logger = KotlinLogging.logger { }

    protected fun debugLog(msg: String) = logger.debug { "${platform.name} ws: $msg" }


    private var lastConnectionTimeMillis: Long = 0L

    fun getFlow(): JsonStringDataFlow = flow {
        while (true) {
            try {

                val currentTimeMilli = Instant.now().toEpochMilli()
                val timeFromLastConnectionMilli = currentTimeMilli - lastConnectionTimeMillis
                delay(reconnectTimeoutMillis - timeFromLastConnectionMilli)
                lastConnectionTimeMillis = Instant.now().toEpochMilli()

                rawMarketDataFlow().collect {
                    emit(it)
                }

            } catch (e: Throwable) {
                commonLogger.error(e) { "Failed to connect to ${platform.name}" }
            }
        }
    }

    protected abstract suspend fun DefaultClientWebSocketSession.receiveData(): JsonStringDataFlow

    private suspend fun rawMarketDataFlow() = flow {
        val client = HttpClient(CIO) {
            install(WebSockets)
            install(HttpTimeout) {
                socketTimeoutMillis = this@WebsocketClient.socketTimeoutMillis
            }
        }

        client.wss(urlString = platform.websocketAddress) {
            receiveData().collect {
                emit(it)
            }
        }

    }
}

