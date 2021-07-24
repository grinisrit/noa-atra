package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.BitstampPlatform

import com.grinisrit.crypto.common.websocket.SeveralRequestWebsocketClient

fun BitstampPlatform.createBitstampSource(requests:  List<String>): BitstampWebsocketSource {
    return BitstampWebsocketSource(this, requests)
}

class BitstampWebsocketSource
internal constructor(
    platform: BitstampPlatform,
    requests: List<String>
) : SeveralRequestWebsocketClient(
    platform,
    requests,
    backendReconnectTimeout = 5000L,
    socketTimeoutMillis = 82000L
)