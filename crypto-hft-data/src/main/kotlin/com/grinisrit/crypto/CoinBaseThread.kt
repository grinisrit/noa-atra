package com.grinisrit.crypto

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.*


const val incomingCheckDelay = 2000L
const val reconnectDelay = 5000L
const val coinbaseReconnectDelay = 4000L


class CoinBaseThread(private val coinbase: Coinbase, zeroMQ: ZeroMQ) : Thread() {

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    var lastConnectionTime: Long = 0L

    private val zeroMQAddress = "tcp://${zeroMQ.address}:${zeroMQ.port}"

    private val loggerFile = PrintStream(BufferedOutputStream(FileOutputStream("log.txt")), true)

    override fun run() {
        val context = ZContext()
        val socket = context.createSocket(SocketType.PUB)
        socket.bind(zeroMQAddress)

        runBlocking {
            while (true) {
                try {
                    loggerFile.println("Trying to connect...")
                    loggerFile.println(calendar.time)

                    val currentTime = calendar.timeInMillis

                    val delta = currentTime - lastConnectionTime

                    delay(coinbaseReconnectDelay - delta)

                    lastConnectionTime = calendar.timeInMillis
                    info().collect {
                        socket.send(it)
                    }
                } catch (e: Throwable) {
                    loggerFile.println("Catch $e")
                    loggerFile.println(calendar.time)
                    delay(reconnectDelay)
                }

            }
        }

    }

    private val request = File("request.txt").readText()


    private fun info() = flow {

        val client = HttpClient(Java) {
            install(WebSockets)
        }


        client.wss(host = coinbase.address) {
            loggerFile.println("Connected successfully")
            loggerFile.println(calendar.time)
            send(Frame.Text(request))
            val subResponse = incoming.receive()
            subResponse as? Frame.Text ?: throw Exception("Bad response")
            loggerFile.println("Request sent. Server response:")
            loggerFile.println(subResponse.readText())

            var gotAnswer = true

            val timer = launch {
                while (true) {
                    gotAnswer = false
                    delay(incomingCheckDelay)
                    if (!gotAnswer) {
                        this@wss.cancel()
                    }
                }
            }

            for (frame in incoming) {
                gotAnswer = true
                frame as? Frame.Text ?: continue
                val dateTime = cbFormat(calendar.time)
                emit("${frame.readText()}///<>///$dateTime")
            }







        }

        client.close()


    }
}