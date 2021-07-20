package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import org.zeromq.ZMQ

open class SingleRequestWebsocketClient(
    platform: Platform,
    socket : ZMQ.Socket,
    request: String,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L,
    logFilePath: String = "platforms/${platform.name}/log.txt"
) : SeveralRequestWebsocketClient(platform, socket, listOf(request), backendReconnectTimeout, socketTimeoutMillis, logFilePath)