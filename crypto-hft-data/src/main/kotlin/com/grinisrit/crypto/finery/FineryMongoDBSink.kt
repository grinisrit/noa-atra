package com.grinisrit.crypto.finery

import com.grinisrit.crypto.common.MarketDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName

fun MongoDBServer.createFinerySink() = FineryMongoDBSink(this)

class FineryMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.finery,
    FineryDataType.values()
) {
    override suspend fun consume(marketDataFlow: MarketDataFlow) =
        handleFlow<FineryData>(marketDataFlow)
}
