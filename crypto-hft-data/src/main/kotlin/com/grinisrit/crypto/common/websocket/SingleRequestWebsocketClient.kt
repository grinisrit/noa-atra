package com.grinisrit.crypto.common.websocket

import com.grinisrit.crypto.Platform

open class SingleRequestWebsocketClient
internal constructor(
    platform: Platform,
    request: String,
    backendReconnectTimeout: Long = 5000L,
    socketTimeoutMillis: Long = 2000L,
    aliveBound: Int = 10000,
) : SeveralRequestWebsocketClient(
    platform,
    listOf(request),
    backendReconnectTimeout,
    socketTimeoutMillis,
    aliveBound
)
