package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.MarketDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName


fun MongoDBServer.createKrakenSink() = KrakenMongoDBSink(this)

class KrakenMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.kraken,
    KrakenDataType.values()
) {
    override suspend fun consume(marketDataFlow: MarketDataFlow) =
        handleFlow<KrakenData>(marketDataFlow)
}
