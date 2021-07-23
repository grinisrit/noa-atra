package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient
import org.zeromq.ZMQ

class BinanceWebsocketClient(
    platform: BinancePlatform,
    socket: ZMQ.Socket,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    socket,
    request,
)