package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class BinanceMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.binance.toString())

    fun loadSnapshots(symbolRaw: String): unrefinedDataFlow {
        val symbol = symbolRaw.uppercase()
        val collection = database.getCollection<TimestampedSnapshot>(BinanceDataType.snapshot.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / BinanceSnapshot::symbol eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadUpdates(symbolRaw: String): unrefinedDataFlow {
        val symbol = symbolRaw.uppercase()
        val collection = database.getCollection<TimestampedUpdate>(BinanceDataType.update.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / BinanceSnapshot::symbol eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadTrades(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedTrade>(BinanceDataType.trade.toString())
        return collection.find(
            TimestampedTrade::platform_data / BinanceTrade::symbol eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

}