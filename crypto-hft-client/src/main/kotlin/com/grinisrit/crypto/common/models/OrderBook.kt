package com.grinisrit.crypto.common.models

class OrderBook(
    val asks: AsksArray,
    val bids: BidsArray,
    //micros
    val timestamp: Long,
) {

    fun updateAsks(price: Float, amount: Float, timestamp: Long, maxSize: Int? = null) = OrderBook(
        asks.update(price, amount, maxSize),
        bids,
        timestamp
    )

    fun updateBids(price: Float, amount: Float, timestamp: Long, maxSize: Int? = null) = OrderBook(
        asks,
        bids.update(price, amount, maxSize),
        timestamp
    )

    val isInvalid = asks.prices.first() <= bids.prices.first() || asks.isInvalid || bids.isInvalid

}
