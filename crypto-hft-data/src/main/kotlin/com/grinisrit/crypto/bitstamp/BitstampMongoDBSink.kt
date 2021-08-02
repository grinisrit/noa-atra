package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.MarketDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName



fun MongoDBServer.createBitstampSink() = BitstampMongoDBSink(this)

class BitstampMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.bitstamp,
    BitstampDataType.values()
) {
    override suspend fun consume(marketDataFlow: MarketDataFlow) =
        handleFlow<BitstampData>(marketDataFlow)
}

