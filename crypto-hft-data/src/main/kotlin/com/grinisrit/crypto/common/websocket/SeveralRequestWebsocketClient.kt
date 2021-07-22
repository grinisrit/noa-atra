package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.flow
import org.zeromq.ZMQ
import java.time.Instant

open class SeveralRequestWebsocketClient(
    platform: Platform,
    socket: ZMQ.Socket,
    private val requests: List<String>,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L,
    logFilePath: String = "platforms/${platform.name}/log.txt"
): WebsocketClient(
    platform,
    socket,
    backendReconnectTimeout,
    socketTimeoutMillis,
    logFilePath
) {
    protected fun dataStringOf(data: String) =
        DataTransport.dataStringOf(platform.name, Instant.now(), data)
    // TODO() make this function better
    override suspend fun DefaultClientWebSocketSession.receiveData() = flow {
        loggerFile.log("Connected successfully")

        for (request in requests){
            loggerFile.log("Sending request:\n$request")
            send(Frame.Text(request))
            /*
            val subResponse = incoming.receive()
            subResponse as? Frame.Text ?: throw Error(subResponse.toString()) // TODO
            loggerFile.log("Request sent. Server response: ${subResponse.readText()}") // TODO() better response

             */
        }

        for (frame in incoming) {
            frame as? Frame.Text ?: throw Error(frame.toString()) // TODO
            //loggerFile.log(frame.readText())
            emit(dataStringOf(frame.readText()))
        }
        
    }
}