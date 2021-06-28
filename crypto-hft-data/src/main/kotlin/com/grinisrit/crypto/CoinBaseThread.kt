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

class CoinBaseThread : Thread() {

    override fun run() {
        val context = ZContext()
        val socket = context.createSocket(SocketType.PUB)
        socket.bind("tcp://localhost:5897")

        runBlocking {
            info().collect { socket.send(it) }
        }

    }

    private val request = File("request.txt").readText()

    private fun info() = flow {
        val client = HttpClient(Java) {
            install(WebSockets)
        }
        client.wss(host = "ws-feed.pro.coinbase.com") {
            send(Frame.Text(request))
            println(incoming.receive())
            while (true) {
                val frame = incoming.receive()
                when (frame) {
                    is Frame.Text -> emit(frame.readText())
                }
            }

        }
    }
}