package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.models.*

const val contractSize = 1e5

fun List<OrderData>.toArrays(): Pair<LongArray, LongArray> {
    val prices = LongArray(size) { i ->
        get(i).price.times(contractSize).toLong()
    }
    val amounts = LongArray(size) { i ->
        get(i).amount.times(contractSize).toLong()
    }

    return Pair(prices, amounts)
}

fun OrderBook.toLocalOrderBook(): LocalOrderBook {
    val asks = with(data.asks.toArrays()){
        AsksArray(
            first,
            second
        )
    }
    val bids = with(data.bids.toArrays()){
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