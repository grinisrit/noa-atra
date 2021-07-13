package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.CoinbasePlatform
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient
import com.grinisrit.crypto.common.websocket.WebsocketClient
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.flow
import java.time.Instant

class BinanceWebsocketClient(
    platform: BinancePlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
    backendReconnectTimeout = 10000L // TODO()!!!!
)