package com.grinisrit.crypto.bitstamp

fun getCost(amount: Float, data: List<OrderData>): Float {
    val size = data.size

    var rest = amount
    var cost = 0.0F

    for (i in 0 until size) {
        if (data[i].amount > rest) {
            cost += rest * data[i].price
            rest = 0.0F
            break
        }
        cost += data[i].amount * data[i].price
        rest -= data[i].amount
    }

    if (rest != 0.0F){
        return 0.0F
    }

    return cost
}

fun OrderBook.getBAS(amount: Float): Float {
    return (getCost(amount, data.asks) - getCost(amount, data.bids))/amount
}