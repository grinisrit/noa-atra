package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.logger
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
    socketTimeoutMillis: Long = 2000L
): WebsocketClient(
    platform,
    socket,
    backendReconnectTimeout,
    socketTimeoutMillis
) {
    protected fun dataStringOf(data: String) =
        DataTransport.dataStringOf(platform.name, Instant.now(), data)
    // TODO() make this function better
    override suspend fun DefaultClientWebSocketSession.receiveData() = flow {
        // TODO Andrei: more informative log messages
        logger.debug { "${platform.name} connected successfully" }

        for (request in requests){
            logger.debug {"Sending request:\n$request"}
            send(Frame.Text(request))
        }

        for (frame in incoming) {
            frame as? Frame.Text ?: throw Error(frame.toString()) // TODO log
            //loggerFile.log(frame.readText())
            emit(dataStringOf(frame.readText()))
        }
        
    }
}