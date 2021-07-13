package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.KrakenPlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient


class KrakenWebsocketClient(
    platform: KrakenPlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 5000L,
    socketTimeoutMillis = 2000L
)