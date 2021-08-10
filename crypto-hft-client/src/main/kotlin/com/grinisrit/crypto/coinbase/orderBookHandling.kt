package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.models.*
import java.time.Instant
import java.util.concurrent.TimeUnit

fun List<OrderData>.toArrays(): Pair<FloatArray, FloatArray> {
    val prices = FloatArray(size) { i ->
        get(i).price
    }
    val amounts = FloatArray(size) { i ->
        get(i).amount
    }

    return Pair(prices, amounts)
}

fun CoinbaseSnapshot.toOrderBook(): OrderBook {
    val asks = with(asks.toArrays()) {
        AsksArray(
            first,
            second
        )
    }
    val bids = with(bids.toArrays()) {
        BidsArray(
            first,
            second
        )
    }
    return OrderBook(
        asks,
        bids,
        -1L,
    )
}

fun OrderBook.update(update: CoinbaseL2Update): OrderBook {
    var orderBook = this
    val timestamp = with(update.datetime) {
        TimeUnit.SECONDS.toMicros(getEpochSecond()) + TimeUnit.NANOSECONDS.toMicros(getNano().toLong());
    }
    update.changes.forEach {
        if (it.side == "sell") {
            orderBook = orderBook.updateAsks(it.price, it.amount, timestamp)
        } else {
            orderBook = orderBook.updateBids(it.price, it.amount, timestamp)
        }
    }
    return orderBook
}