package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.flow
import java.time.Instant

open class SeveralRequestWebsocketClient(
    platform: Platform,
    private val requests: List<String>,
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

        for (request in requests){
            loggerFile.log("Sending request: $request")
            send(Frame.Text(request))
            val subResponse = incoming.receive()
            subResponse as? Frame.Text ?: throw Error(subResponse.toString()) // TODO
            loggerFile.log("Request sent. Server response: ${subResponse.readText()}") // TODO() better response
        }


        for (frame in incoming) {
            frame as? Frame.Text ?: throw Error(frame.toString()) // TODO
            emit(DataTransport.dataStringOf(platform.platformName, Instant.now(), frame.readText()))
        }
        
    }
}