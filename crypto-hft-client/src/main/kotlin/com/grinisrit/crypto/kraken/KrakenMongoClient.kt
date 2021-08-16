package com.grinisrit.crypto.kraken


import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class KrakenMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.kraken.toString())

    fun loadSnapshots(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedSnapshot>(KrakenDataType.snapshot.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / KrakenBookSnapshot::pair eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadUpdates(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedUpdate>(KrakenDataType.update.toString())
        return collection.find(
            TimestampedUpdate::platform_data / KrakenBookUpdate::pair eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadTrades(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedTrade>(KrakenDataType.trade.toString())
        return collection.find(
            TimestampedTrade::platform_data / KrakenTrade::pair eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }


}