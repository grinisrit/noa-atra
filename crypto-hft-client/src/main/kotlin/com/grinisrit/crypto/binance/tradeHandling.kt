package com.grinisrit.crypto.binance

import com.grinisrit.crypto.analysis.toEpochMicro
import com.grinisrit.crypto.common.models.Trade

fun BinanceTrade.toTrade(): Trade =
    Trade(
        price,
        quantity,
        eventTime * 1000L, // TODO!!!!
        if (isMarketMaker) {
            Trade.Type.BUY
        } else {
            Trade.Type.SELL
        }
    )
