package com.grinisrit.crypto.common.models

class OrderBook(
    val asks: AsksArray,
    val bids: BidsArray,
    //micros
    val timestamp: Long,
) {

    fun updateAsks(price: Float, amount: Float, timestamp: Long) = OrderBook(
        asks.update(price, amount),
        bids,
        timestamp
    )

    fun updateBids(price: Float, amount: Float, timestamp: Long) = OrderBook(
        asks,
        bids.update(price, amount),
        timestamp
    )

    fun updateAsks(action: OrdersArray.Action, price: Float, amount: Float, timestamp: Long) = OrderBook(
        asks.update(action, price, amount),
        bids,
        timestamp
    )

    fun updateBids(action: OrdersArray.Action, price: Float, amount: Float, timestamp: Long) = OrderBook(
        asks,
        bids.update(action, price, amount),
        timestamp
    )

    val isInvalid = asks.prices.first() <= bids.prices.first() || asks.isInvalid || bids.isInvalid

}
