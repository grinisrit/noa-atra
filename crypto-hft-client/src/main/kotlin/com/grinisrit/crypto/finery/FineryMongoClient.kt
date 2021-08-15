package com.grinisrit.crypto.finery


import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class FineryMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.finery.toString())

    fun loadSnapshots(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedSnapshot>(FineryDataType.snapshot.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / FinerySnapshot::symbol eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadUpdates(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedUpdates>(FineryDataType.updates.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / FineryUpdates::symbol eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }
    /* TODO
    fun loadTrades(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedTrade>(CoinbaseDataType.match.toString())
        return collection.find(
            TimestampedTrade::platform_data / CoinbaseMatch::product_id eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

     */

}