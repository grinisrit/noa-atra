package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.MarketDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName

fun MongoDBServer.createDeribitSink() = DeribitMongoDBSink(this)

class DeribitMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.deribit,
    DeribitDataType.values()
) {
    override suspend fun consume(marketDataFlow: MarketDataFlow) =
        handleFlow<DeribitData>(marketDataFlow)
}