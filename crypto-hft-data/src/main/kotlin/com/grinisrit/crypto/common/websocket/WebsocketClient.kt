package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
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
    protected val platform: Platform,
    private val socket: ZMQ.Socket,
    private val reconnectTimeoutMillis: Long = 5000L,
    private val socketTimeoutMillis: Long = 2000L,
    logFilePath: String = "platforms/${platform.platformName}/log.txt"
)  {

    var lastConnectionTimeMilli: Long = 0L

    protected val loggerFile = PrintStream(BufferedOutputStream(FileOutputStream(logFilePath)), true)

    protected fun PrintStream.log(logText: String) {
        this.println("${Instant.now()}; $logText")
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun run() {

            while (true) {
               try {
                    val instant = Instant.now()
                    val currentTimeMilli = instant.toEpochMilli()
                    loggerFile.log("Trying to connect...")

                    val timeFromLastConnectionMilli = currentTimeMilli - lastConnectionTimeMilli

                   // TODO()!!!
                    GlobalScope.launch { delay(reconnectTimeoutMillis - timeFromLastConnectionMilli)}.join()

                    lastConnectionTimeMilli = Instant.now().toEpochMilli()


                    dataFlow().collect {
                        socket.send(it)
                    }

                } catch (e: Throwable) {
                   loggerFile.log("Catch $e")
               }

            }


    }

    abstract suspend fun DefaultClientWebSocketSession.receiveData(): Flow<String>

    open suspend fun dataFlow() = flow {
        // TODO()
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

