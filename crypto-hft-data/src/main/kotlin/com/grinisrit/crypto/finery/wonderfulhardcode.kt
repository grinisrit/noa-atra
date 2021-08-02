package com.grinisrit.crypto.finery

val symbolToFeedId = mapOf(
    "BTC-USD" to 4955410050,
    "ETH-USD" to 4955415173,
    "ETH-BTC" to 3895304837,
)

val feedIdToSymbol = symbolToFeedId.map {
    it.value to it.key
}.toMap()