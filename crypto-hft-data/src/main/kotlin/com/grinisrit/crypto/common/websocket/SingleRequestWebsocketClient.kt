package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.flow
import java.time.Instant

open class SingleRequestWebsocketClient(
    platform: Platform,
    request: String,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L,
    logFilePath: String = "platforms/${platform.platformName}/log.txt"
) : SeveralRequestWebsocketClient(platform, listOf(request), backendReconnectTimeout, socketTimeoutMillis, logFilePath)