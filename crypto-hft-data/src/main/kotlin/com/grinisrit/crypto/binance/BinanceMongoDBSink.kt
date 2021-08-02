package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.MarketDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName


fun MongoDBServer.createBinanceSink() = BinanceMongoDBSink(this)

class BinanceMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.binance,
    BinanceDataType.values()
) {
    override suspend fun consume(marketDataFlow: MarketDataFlow) =
        handleFlow<BinanceData>(marketDataFlow)
}