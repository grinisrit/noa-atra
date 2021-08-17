package com.grinisrit.crypto.analysis

import com.grinisrit.crypto.common.models.OrderBook

fun OrderBook.getBidAskSpread(amount: Float): Float? {
    val askCost = asks.getCost(amount)
    val bidCost = bids.getCost(amount)

    return if (askCost == null || bidCost == null) {
        null
    } else {
        (askCost - bidCost) / (0.5F * (askCost + bidCost))
    }
}

fun OrderBook.getMidPrice(amount: Float): Float? {
    val askCost = asks.getCost(amount)
    val bidCost = bids.getCost(amount)

    return if (askCost == null || bidCost == null) {
        null
    } else {
        0.5F * (askCost + bidCost)
    }
}

fun OrderBook.getAskSpread(amount: Float): Float? {
    val askCost = asks.getCost(amount)
    val bidCost = bids.getCost(amount)

    return if (askCost == null || bidCost == null) {
        null
    } else {
        askCost / (0.5F * (askCost + bidCost)) - 1.0F
    }
}

fun OrderBook.getBidSpread(amount: Float): Float? {
    val askCost = asks.getCost(amount)
    val bidCost = bids.getCost(amount)

    return if (askCost == null || bidCost == null) {
        null
    } else {
        1.0F - bidCost / (0.5F * (askCost + bidCost))
    }
}