package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.DeribitPlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient


fun DeribitPlatform.createDeribitSource(request: String): DeribitWebsocketSource {
    return DeribitWebsocketSource(this, request)
}

class DeribitWebsocketSource(
    platform: DeribitPlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 1000L,
    socketTimeoutMillis = 5000L,
    aliveBound = 500
)
