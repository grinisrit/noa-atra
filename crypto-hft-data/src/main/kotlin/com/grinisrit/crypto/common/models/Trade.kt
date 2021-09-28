package com.grinisrit.crypto.common.models

// TODO() what parameters?? id?
class Trade(
    val price: Float,
    val amount: Float,
    //micros
    val timestamp: Long,
    val type: Type
) {
    enum class Type {
        SELL, BUY;
    }
}
