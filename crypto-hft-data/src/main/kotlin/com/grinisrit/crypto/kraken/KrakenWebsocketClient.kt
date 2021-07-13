package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.KrakenPlatform
import com.grinisrit.crypto.common.websocket.SeveralRequestWebsocketClient


class KrakenWebsocketClient(
    platform: KrakenPlatform,
    requests: List<String>
) : SeveralRequestWebsocketClient(
    platform,
    requests,
    backendReconnectTimeout = 5000L,
    socketTimeoutMillis = 2000L
)