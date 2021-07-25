package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.TimestampedDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName

fun MongoDBServer.createDeribitSink() = DeribitMongoDBSink(this)

class DeribitMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.deribit,
    listOf("book", "trades")
) {
    override suspend fun consume(marketDataFlow: TimestampedDataFlow) =
        handleFlow<DeribitData>(marketDataFlow)
}