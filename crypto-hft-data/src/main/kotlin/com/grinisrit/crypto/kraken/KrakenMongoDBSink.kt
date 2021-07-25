package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.TimestampedDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName


fun MongoDBServer.createKrakenSink() = KrakenMongoDBSink(this)

class KrakenMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.kraken,
    listOf("snapshot", "trade", "update")
) {
    override suspend fun consume(marketDataFlow: TimestampedDataFlow) =
        handleFlow<KrakenData>(marketDataFlow)
}
