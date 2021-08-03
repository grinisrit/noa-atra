package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.models.*

//const val contractSize = 1e5

fun List<OrderData>.toArrays(): Pair<FloatArray, FloatArray> {
    val prices = FloatArray(size) { i ->
        get(i).price
    }
    val amounts = FloatArray(size) { i ->
        get(i).amount
    }

    return Pair(prices, amounts)
}

fun OrderBook.toLocalOrderBook(): LocalOrderBook {
    val asks = with(data.asks.toArrays()) {
        AsksArray(
            first,
            second
        )
    }
    val bids = with(data.bids.toArrays()) {
        BidsArray(
            first,
            second
        )
    }
    return LocalOrderBook(
        asks,
        bids,
        data.microtimestamp / 1000
    )
}