package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.models.*
import kotlin.math.max

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

fun OrderBook.update(update: CoinbaseL2Update, maxSize: Int? = 1000): OrderBook {
    var orderBook = this
    val timestamp = update.datetime.toEpochMicro()
    update.changes.forEach {
        if (it.side == "sell") {
            orderBook = orderBook.updateAsks(it.price, it.amount, timestamp, maxSize)
        } else {
            orderBook = orderBook.updateBids(it.price, it.amount, timestamp, maxSize)
        }
    }
    return orderBook
}