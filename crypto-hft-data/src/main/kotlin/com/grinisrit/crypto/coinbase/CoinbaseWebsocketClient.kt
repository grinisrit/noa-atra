package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.CoinbasePlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient


fun CoinbasePlatform.createCoinbaseSource(request: String): CoinbaseWebsocketClient {
    return CoinbaseWebsocketClient(this, request)
}

class CoinbaseWebsocketClient
internal constructor(
    platform: CoinbasePlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 4000L
)