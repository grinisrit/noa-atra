package com.grinisrit.crypto

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.time.Instant


abstract class PlatformWebsocketClient(
    protected val platformName: String,
    private val address: String,
    private val zeroMQPubSocket: ZMQ.Socket,
    private val backendReconnectTimeout: Long = 5000L,
    private val incomingCheckDelay: Long = 2000L,
    logFilePath: String = "$platformName/log.txt"
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

        client.wss(host = address) {
            this.receiveData().collect { emit(it) }
        }
        client.close()
    }
}

class CoinbaseWebsocketClient(
    address: String,
    zeroMQPubSocket: ZMQ.Socket,
    private val request: String
) : PlatformWebsocketClient(
    "coinbase",
    address,
    zeroMQPubSocket,
    backendReconnectTimeout = 4000L
) {
    // TODO() make this function better
    override fun DefaultClientWebSocketSession.receiveData() = flow {
        loggerFile.log("Connected successfully")
        send(Frame.Text(request))
        val subResponse = incoming.receive()
        subResponse as? Frame.Text ?: throw Exception("Invalid response")
        loggerFile.log("Request sent. Server response: ${subResponse.readText()}")

        for (frame in incoming) {
            frame as? Frame.Text ?: throw Error(frame.toString()) // TODO
            val res = DataTransport.dataStringOf(platformName, Instant.now(), frame.readText())
            emit(res)
        }
    }
}
