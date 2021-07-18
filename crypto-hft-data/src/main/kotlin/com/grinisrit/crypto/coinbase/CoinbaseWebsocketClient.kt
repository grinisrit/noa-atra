package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.CoinbasePlatform
import com.grinisrit.crypto.common.websocket.SingleRequestWebsocketClient
import org.zeromq.ZMQ


class CoinbaseWebsocketClient(
    platform: CoinbasePlatform,
    socket : ZMQ.Socket,
    request: String
) : SingleRequestWebsocketClient(
    platform,
    socket,
    request,
    backendReconnectTimeout = 4000L
)