package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient

fun BinancePlatform.createBinanceSource(request: String): BinanceWebsocketSource {
    return BinanceWebsocketSource(this, request)
}

class BinanceWebsocketSource
    internal constructor(
    platform: BinancePlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    aliveBound = 5000,
)