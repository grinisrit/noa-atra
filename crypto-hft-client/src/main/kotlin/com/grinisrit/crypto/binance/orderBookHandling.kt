package com.grinisrit.crypto.binance

import com.grinisrit.crypto.analysis.toEpochMicro
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

// TODO
fun BinanceSnapshot.toOrderBook(): OrderBook {
    val asks = with(snapshot.asks.toArrays()) {
        AsksArray(
            first,
            second
        )
    }
    val bids = with(snapshot.bids.toArrays()) {
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

fun OrderBook.update(update: BinanceBookUpdate): OrderBook {
    var orderBook = this
    val timestamp = update.eventTime * 1000L

    update.asks.forEach {
        orderBook = orderBook.updateAsks(it.price, it.amount, timestamp)
    }
    update.bids.forEach {
        orderBook = orderBook.updateBids(it.price, it.amount, timestamp)
    }

    return orderBook
}
