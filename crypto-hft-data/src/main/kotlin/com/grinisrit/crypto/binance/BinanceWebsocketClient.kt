package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import org.apache.http.HttpResponse

class BinanceWebsocketClient(
    platform: BinancePlatform,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    request,
) {
    override fun dataFlow() = flow {

        // TODO() remove hardcode and logging
        HttpClient().use {
            emit(dataStringOf(it.get("https://api.binance.com/api/v3/depth?symbol=BTCUSDT&limit=1000")))
            emit(dataStringOf(it.get("https://api.binance.com/api/v3/depth?symbol=ETHUSDT&limit=1000")))
            emit(dataStringOf(it.get("https://api.binance.com/api/v3/depth?symbol=ETHBTC&limit=1000")))
        }

        super.dataFlow().collect { emit(it) }
    }
}