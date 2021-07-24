package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.BitstampPlatform
import com.grinisrit.crypto.KrakenPlatform
import com.grinisrit.crypto.common.websocket.SeveralRequestWebsocketClient
import org.zeromq.ZMQ

class BitstampWebsocketClient(
    platform: BitstampPlatform,
    socket : ZMQ.Socket,
    requests: List<String>
) : SeveralRequestWebsocketClient(
    platform,
    socket,
    requests,
    backendReconnectTimeout = 5000L,
    socketTimeoutMillis = 82000L
)