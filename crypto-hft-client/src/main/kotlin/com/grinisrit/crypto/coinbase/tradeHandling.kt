package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.models.Trade

fun CoinbaseMatch.toTrade(): Trade =
    Trade(
        price,
        size,
        datetime.toEpochMicro(),
        if (side == "buy") {
            Trade.Type.BUY
        } else {
            Trade.Type.SELL
        }
    )
