package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.common.DataTransport
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.zeromq.ZMQ
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.PrintStream
import java.time.Instant


abstract class WebsocketClient(
    protected val platformName: String,
    private val address: String,
    private val zeroMQPubSocket: ZMQ.Socket,
    private val backendReconnectTimeout: Long = 5000L,
    private val incomingCheckDelay: Long = 2000L,
    logFilePath: String = "platforms/$platformName/log.txt"
) : Thread() {

    var lastConnectionTimeMilli: Long = 0L

    protected val loggerFile = PrintStream(BufferedOutputStream(FileOutputStream(logFilePath)), true)

    protected fun PrintStream.log(logText: String) {
        this.println("${Instant.now()}; $logText")
    }

    override fun run() {

        runBlocking {
            while (true) {
                try {
                    val instant = Instant.now()
                    val currentTimeMilli = instant.toEpochMilli()
                    loggerFile.log("Trying to connect...")

                    val timeFromLastConnectionMilli = currentTimeMilli - lastConnectionTimeMilli

                    delay(backendReconnectTimeout - timeFromLastConnectionMilli)

                    lastConnectionTimeMilli = Instant.now().toEpochMilli()

                    dataFlow().collect {
                        zeroMQPubSocket.send(it)
                    }

                } catch (e: Throwable) {
                    loggerFile.log("Catch $e")
                }

            }
        }

    }

    abstract fun DefaultClientWebSocketSession.receiveData(): Flow<String>

    private fun dataFlow() = flow {

        val client = HttpClient(CIO) {
            install(WebSockets)
            install(HttpTimeout) {
                socketTimeoutMillis = incomingCheckDelay
            }
        }

        client.wss(urlString = address) {
            this.receiveData().collect { emit(it) }
        }

        client.close()

    }
}
