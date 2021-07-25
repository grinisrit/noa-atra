package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.MarkedDataFlow
import com.grinisrit.crypto.common.MongoDBServer
import com.grinisrit.crypto.common.MongoDBSink
import com.grinisrit.crypto.common.PlatformName



fun MongoDBServer.createBitstampSink() = BitstampMongoDBSink(this)

class BitstampMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.bitstamp,
    listOf("order_book", "trade")
) {
    override suspend fun consume(marketDataFlow: MarkedDataFlow) =
        handleFlow<BitstampData, Event>(marketDataFlow)
}

