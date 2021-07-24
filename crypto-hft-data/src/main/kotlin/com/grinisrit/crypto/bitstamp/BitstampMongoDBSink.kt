package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.MarkedDataFlow
import com.grinisrit.crypto.common.MongoDBServer
import com.grinisrit.crypto.common.MongoDBSink
import com.grinisrit.crypto.common.PlatformName



fun MongoDBServer.createBitstampMongoDBSink() = BitstampMongoDBSink(this)

class BitstampMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.bitstamp,
    listOf("snapshot", "trade", "update")
) {
    override suspend fun consume(marketDataFlow: MarkedDataFlow) =
        handleFlow<BitstampData, Event>(marketDataFlow)
}

