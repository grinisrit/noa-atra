package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.DeribitPlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient

class DeribitWebsocketClient(
    platform: DeribitPlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 10000L,
    socketTimeoutMillis = 40000L// TODO()!!!!
)