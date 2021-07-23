package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.KrakenPlatform
import com.grinisrit.crypto.common.websocket.SeveralRequestWebsocketClient
import org.zeromq.ZMQ

class KrakenWebsocketClient(
    platform: KrakenPlatform,
    socket : ZMQ.Socket,
    requests: List<String>
) : SeveralRequestWebsocketClient(
    platform,
    socket,
    requests,
    backendReconnectTimeout = 5000L,
    socketTimeoutMillis = 2000L
)