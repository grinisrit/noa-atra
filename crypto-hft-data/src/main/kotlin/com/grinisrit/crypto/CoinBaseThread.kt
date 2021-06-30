package com.grinisrit.crypto

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.*
import org.litote.kmongo.inc
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val incomingCheckDelay = 2000L
const val reconnectDelay = 5000L
const val coinbaseReconnectDelay = 4000L


class CoinBaseThread(private val coinbase: Coinbase, zeroMQ: ZeroMQ) : Thread() {

    val calendar = Calendar.getInstance()

    var lastConnectionTime: Long = 0L

    private val zeroMQAddress = "tcp://${zeroMQ.address}:${zeroMQ.port}"

    override fun run() {
        val context = ZContext()
        val socket = context.createSocket(SocketType.PUB)
        socket.bind(zeroMQAddress)

        runBlocking {
            while (true) {
                try {
                    println("Trying to connect...")

                    val currentTime = calendar.timeInMillis

                    val delta = currentTime - lastConnectionTime

                    delay(coinbaseReconnectDelay - delta)


                    lastConnectionTime = calendar.timeInMillis
                    info().collect { socket.send(it) }
                } catch (e: Throwable) {
                    println("Catch $e")
                    delay(reconnectDelay)
                }

            }
        }

    }

    private val request = File("request.txt").readText()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun info() = flow {

        val client = HttpClient(Java) {
            install(WebSockets)
        }

        client.wss(host = coinbase.address) {
            println("Connected successfully")
            send(Frame.Text(request))
            when (val frame = incoming.receive()) {
                is Frame.Text -> {
                    println("Request sent. Server response:")
                    println(frame.readText())
                }
            }

            while (true) {

                if (incoming.isEmpty) {
                    println("Incoming is empty. Waiting...")
                    delay(incomingCheckDelay)
                    if (incoming.isEmpty) {
                        println("Disconnect")
                        break
                    }
                }

                when (val frame = incoming.receive()) {
                    is Frame.Text -> {
                        emit(frame.readText())
                    }
                }
            }
        }

    }
}