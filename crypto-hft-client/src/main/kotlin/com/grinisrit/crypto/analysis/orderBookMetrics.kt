package com.grinisrit.crypto.analysis

import com.grinisrit.crypto.common.models.OrderBook

fun OrderBook.getBidAskSpread(amount: Float): Float? {
    val askCost = asks.getCost(amount)
    val bidCost = bids.getCost(amount)

    return if (askCost == null || bidCost == null) {
        null
    } else {
        (askCost - bidCost) / amount
    }
}

fun OrderBook.getMidPrice(): Float = (asks.prices.first() + bids.prices.first()) / 2

fun OrderBook.getAskSpread(amount: Float): Float? = asks.getCost(amount)?.let {
    (it / amount) - getMidPrice()
}

fun OrderBook.getBidSpread(amount: Float): Float? = bids.getCost(amount)?.let {
    getMidPrice() - (it / amount)
}