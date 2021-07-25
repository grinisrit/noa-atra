package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.MarkedDataFlow
import com.grinisrit.crypto.common.MongoDBServer
import com.grinisrit.crypto.common.MongoDBSink
import com.grinisrit.crypto.common.PlatformName

fun MongoDBServer.createDeribitSink() = DeribitMongoDBSink(this)

class DeribitMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.deribit,
    listOf("book", "trades")
) {
    override suspend fun consume(marketDataFlow: MarkedDataFlow) =
        handleFlow<DeribitData>(marketDataFlow)
}