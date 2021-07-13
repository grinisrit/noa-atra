package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.CoinbasePlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient


class CoinbaseWebsocketClient(
    platform: CoinbasePlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 4000L
)