package com.grinisrit.crypto.common.models

class LocalOrderBook(
    val asks: AsksArray,
    val bids: BidsArray,
    //milliseconds
    val timestamp: Long,
) {

    fun updateAsks(price: Long, amount: Long, timestamp: Long) = LocalOrderBook(
        asks.update(price, amount),
        bids,
        timestamp
    )

    fun updateBids(price: Long, amount: Long) = LocalOrderBook(
        asks,
        bids.update(price, amount),
        timestamp
    )

    fun getBAS(amount: Long): Long {
        val askCost = asks.getCost(amount)
        val bidCost = bids.getCost(amount)

        // TODO()
        if (askCost < 0 || bidCost < 0){
            return -100L

        }
        return (asks.getCost(amount) - bids.getCost(amount))/amount
    }
}
