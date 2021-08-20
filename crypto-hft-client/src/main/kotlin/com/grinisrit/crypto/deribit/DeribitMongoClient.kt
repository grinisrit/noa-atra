package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class DeribitMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.deribit.toString())

    fun loadOrderBooks(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedOrderBook>(DeribitDataType.book.toString())
        return collection.find(
            TimestampedOrderBook::platform_data / DeribitBook::params
                    / BookParameters::data / BookData::instrument_name eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadTrades(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedTrade>(DeribitDataType.trades.toString())
        val channel = "trades.$symbol.raw"
        return collection.find(
            TimestampedTrade::platform_data / DeribitTrades::params /
                   TradesParameters::channel eq channel
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

}