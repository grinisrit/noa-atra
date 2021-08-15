package com.grinisrit.crypto.finery

import com.grinisrit.crypto.analysis.toEpochMicro
import com.grinisrit.crypto.common.models.*
import java.time.Instant

const val contractSize = 1e8F

fun List<BookLevel>.toArrays(): Pair<FloatArray, FloatArray> {
    val prices = FloatArray(size) { i ->
        get(i).price / contractSize
    }
    val amounts = FloatArray(size) { i ->
        get(i).size / contractSize
    }

    return Pair(prices, amounts)
}

fun FinerySnapshot.toOrderBook(datetime: Instant): OrderBook {
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
    return OrderBook(
        asks,
        bids,
        datetime.toEpochMicro(),
    )
}

internal fun Char.toAction() = when (this) {
    '+' -> OrdersArray.Action.ADD
    '-' -> OrdersArray.Action.DELETE
    'M' -> OrdersArray.Action.UPDATE
    '~' -> OrdersArray.Action.REMOVE_TOP
    else -> TODO("Exception")
}

fun OrderBook.update(update: FineryUpdates, datetime: Instant): OrderBook {
    var orderBook = this
    val timestamp = datetime.toEpochMicro()
    update.data.asks.forEach {
        orderBook = orderBook.updateAsks(
            it.action.toAction(),
            it.price / contractSize,
            it.size / contractSize,
            timestamp
        )
    }
    update.data.bids.forEach {
        orderBook = orderBook.updateBids(
            it.action.toAction(),
            it.price / contractSize,
            it.size / contractSize,
            timestamp
        )
    }
    return orderBook
}