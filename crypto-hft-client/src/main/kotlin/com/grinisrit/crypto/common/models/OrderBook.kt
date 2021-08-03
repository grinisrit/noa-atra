package com.grinisrit.crypto.common.models

class OrderBook(
    val asks: AsksArray,
    val bids: BidsArray,
    //milliseconds
    val timestamp: Long,
) {

    fun updateAsks(price: Float, amount: Float, timestamp: Long) = OrderBook(
        asks.update(price, amount),
        bids,
        timestamp
    )

    fun updateBids(price: Float, amount: Float) = OrderBook(
        asks,
        bids.update(price, amount),
        timestamp
    )

    val isInvalid = asks.prices.first() <= bids.prices.first() || asks.isInvalid || bids.isInvalid

    fun getBAS(amount: Float): Float? {
        val askCost = asks.getCost(amount)
        val bidCost = bids.getCost(amount)

        return if (askCost == null || bidCost == null) {
            null
        } else {
            (askCost - bidCost) / amount
        }

    }
}
