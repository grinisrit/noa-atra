package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.flow
import java.time.Instant

open class SingleRequestWebsocketClient(
    platform: Platform,
    private val request: String,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L,
    logFilePath: String = "platforms/${platform.platformName}/log.txt"
): WebsocketClient(
    platform,
    backendReconnectTimeout,
    socketTimeoutMillis,
    logFilePath
) {
    // TODO() make this function better
    override fun DefaultClientWebSocketSession.receiveData() = flow {
        loggerFile.log("Connected successfully")

        loggerFile.log("Sending request: $request")
        send(Frame.Text(request))

        val subResponse = incoming.receive()
        subResponse as? Frame.Text ?: throw Exception("Invalid response")
        loggerFile.log("Request sent. Server response: ${subResponse.readText()}")

        for (frame in incoming) {
            frame as? Frame.Text ?: throw Error(frame.toString()) // TODO
         //   loggerFile.log(frame.readText())
            emit(DataTransport.dataStringOf(platform.platformName, Instant.now(), frame.readText()))
        }

        println("wtf")
    }
}