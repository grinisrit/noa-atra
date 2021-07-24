package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.MarkedDataFlow
import com.grinisrit.crypto.common.MongoDBServer
import com.grinisrit.crypto.common.MongoDBSink
import com.grinisrit.crypto.common.PlatformName


fun MongoDBServer.createKrakenSink() = KrakenMongoDBSink(this)

class KrakenMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.kraken,
    listOf("snapshot", "trade", "update")
) {
    override suspend fun consume(marketDataFlow: MarkedDataFlow) =
        handleFlow<KrakenData, Event>(marketDataFlow)
}
