package com.grinisrit.crypto

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.io.File

class CoinBaseThread(private val coinbase: Coinbase, zeroMQ: ZeroMQ) : Thread() {

    private val zeroMQAddress = "tcp://${zeroMQ.address}:${zeroMQ.port}"

    override fun run() {
        val context = ZContext()
        val socket = context.createSocket(SocketType.PUB)
        socket.bind(zeroMQAddress)

        runBlocking {
            info().collect { socket.send(it) }
        }

    }

    private val request = File("request.txt").readText()

    private fun info() = flow {

        val client = HttpClient(Java) {
            install(WebSockets)
        }

        client.wss(host = coinbase.address) {
            send(Frame.Text(request))
            println(incoming.receive())
            while (true) {
                when (val frame = incoming.receive()) {
                    is Frame.Text -> emit(frame.readText())
                }
            }
        }

    }
}