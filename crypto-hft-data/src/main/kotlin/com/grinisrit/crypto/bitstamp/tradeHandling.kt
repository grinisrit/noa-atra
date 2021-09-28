package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.models.Trade

fun BitstampTrade.toTrade(): Trade =
    Trade(
        data.price,
        data.amount,
        data.microtimestamp,
        if (data.type == 0) {
            Trade.Type.BUY
        } else {
            Trade.Type.SELL
        }
    )
