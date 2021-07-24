package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.logger

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.zeromq.ZMQ
import java.time.Instant


abstract class WebsocketClient(
    protected val platform: Platform,
    private val socket: ZMQ.Socket,
    private val reconnectTimeoutMillis: Long = 5000L,
    private val socketTimeoutMillis: Long = 2000L
) {

    var lastConnectionTimeMilli: Long = 0L

    suspend fun run() {

        while (true) {
            try {
                val instant = Instant.now()
                val currentTimeMilli = instant.toEpochMilli()

                val timeFromLastConnectionMilli = currentTimeMilli - lastConnectionTimeMilli

                delay(reconnectTimeoutMillis - timeFromLastConnectionMilli)

                lastConnectionTimeMilli = Instant.now().toEpochMilli()

                dataFlow().collect {
                    socket.send(it)
                }

            } catch (e: Throwable) {
                // TODO Andrei: better error message
                logger.error(e) { " ${platform.name} failed to launch flow" }
            }

        }

    }

    abstract suspend fun DefaultClientWebSocketSession.receiveData(): Flow<String>

    suspend fun dataFlow() = flow {

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

