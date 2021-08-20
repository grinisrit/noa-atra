package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.models.Trade

fun TradeData.toTrade(): Trade =
    Trade(
        mark_price,
        amount,
        timestamp * 1000,
        if (direction == "buy") {
            Trade.Type.BUY
        } else {
            Trade.Type.SELL
        }
    )
