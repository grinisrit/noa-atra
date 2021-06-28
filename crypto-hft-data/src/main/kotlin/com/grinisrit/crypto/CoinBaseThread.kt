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

class CoinBaseThread : Thread() {

    override fun run() {
        val context = ZContext()
        val socket = context.createSocket(SocketType.PUB)
        socket.bind("tcp://localhost:5897")

        runBlocking {
            info().collect { socket.send(it) }
        }

    }

    val req = "{\n" +
            "    \"type\": \"subscribe\",\n" +
            "    \"product_ids\": [\n" +
            "        \"ETH-USD\",\n" +
            "        \"ETH-EUR\"\n" +
            "    ],\n" +
            "    \"channels\": [\n" +
            "        \"heartbeat\"\n" +
            "    ]\n" +
            "}"

    private fun info() = flow {
        val client = HttpClient(Java) {
            install(WebSockets)
        }
        client.wss(host = "ws-feed.pro.coinbase.com") {
            send(Frame.Text(req))
            incoming.receive() // todo make sense
            while (true) {
                val frame = incoming.receive()
                when (frame) {
                    is Frame.Text -> emit(frame.readText())
                }
            }

        }
    }
}