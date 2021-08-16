package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.analysis.toEpochMicro
import com.grinisrit.crypto.common.models.*
import java.time.Instant

fun List<OrderData>.toArrays(): Pair<FloatArray, FloatArray> {
    val prices = FloatArray(size) { i ->
        get(i).price
    }
    val amounts = FloatArray(size) { i ->
        get(i).volume
    }

    return Pair(prices, amounts)
}

// TODO time
fun KrakenBookSnapshot.toOrderBook(datetime: Instant): OrderBook {
    val asks = with(bookData.asks.toArrays()) {
        AsksArray(
            first,
            second
        )
    }
    val bids = with(bookData.bids.toArrays()) {
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

fun OrderBook.update(update: KrakenBookUpdate, datetime: Instant): OrderBook {
    var orderBook = this
    val timestamp = datetime.toEpochMicro()
    update.asksUpdate?.a?.forEach {
        if (it.updateType != null) {
            return@forEach // TODO
        }
        orderBook = orderBook.updateAsks(
            it.price,
            it.volume,
            timestamp
        )
    }
    update.bidsUpdate?.b?.forEach {
        if (it.updateType != null) {
            return@forEach // TODO
        }
        orderBook = orderBook.updateBids(
            it.price,
            it.volume,
            timestamp
        )
    }
    return orderBook
}