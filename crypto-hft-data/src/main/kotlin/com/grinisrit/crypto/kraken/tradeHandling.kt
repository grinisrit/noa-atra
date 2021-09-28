package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.analysis.toEpochMicro
import com.grinisrit.crypto.common.models.Trade

fun TradeData.toTrade(): Trade =
    Trade(
        price,
        volume,
        datetime.toEpochMicro(), // TODO!
        if (side == "b") {
            Trade.Type.BUY
        } else {
            Trade.Type.SELL
        }
    )
