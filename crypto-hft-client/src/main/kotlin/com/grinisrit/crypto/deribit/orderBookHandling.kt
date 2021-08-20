package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.models.*

fun List<OrderData>.toArrays(): Pair<FloatArray, FloatArray> {
    val prices = FloatArray(size) { i ->
        get(i).price
    }
    val amounts = FloatArray(size) { i ->
        get(i).amount
    }

    return Pair(prices, amounts)
}

fun DeribitBook.toOrderBook(): OrderBook {
    val asks = with(params.data.asks.toArrays()) {
        AsksArray(
            first,
            second
        )
    }
    val bids = with(params.data.bids.toArrays()) {
        BidsArray(
            first,
            second
        )
    }
    return OrderBook(
        asks,
        bids,
        params.data.timestamp * 1000
    )
}