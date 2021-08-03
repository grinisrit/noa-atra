package com.grinisrit.crypto.common.mongo

import com.grinisrit.crypto.*
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.models.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

suspend fun MongoDBConfig.getMongoDBServer(): MongoDBServer {
    val mongo = KMongo.createClient(this.address).coroutine
    commonLogger.debug { "Connecting to MongoDB at ${this.address} ..." }
    val dbs = mongo.listDatabaseNames()
    commonLogger.debug { "Connection to MongoDB established, found ${dbs.size} databases" }
    return MongoDBServer(mongo)
}

class MongoDBServer internal constructor(val client: CoroutineClient)

abstract class MongoDBSink constructor(
    private val server: MongoDBServer,
    val platformName: PlatformName,
    dataTypes: Array<out DataType>
) {

    protected var numEntities = 0L

    protected val logger = KotlinLogging.logger { }

    protected fun debugLog(msg: String) = logger.debug { "$platformName mongo: $msg" }

    private val database = server.client.getDatabase(platformName.toString())

    protected val nameToCollection = dataTypes.associateWith {
        database.getCollection<TimestampedMarketData>(it.toString())
    }

    fun getCollection(dataType: DataType) = nameToCollection[dataType]

    fun sentinelLog(){
        debugLog("persisted $numEntities entities")
    }

    protected suspend inline fun<reified Data: PlatformData>
            handleFlow(marketDataFlow: MarketDataFlow) =
        marketDataFlow
            .filter { it.platform_data is Data }
            .filter { it.platform_data !is UnbookedEvent }
            .collect {
                val collectionName = it.platform_data.type
                val col = nameToCollection[collectionName]
                if (col!=null) {
                    col.insertOne(it)
                    numEntities += 1
                } else {
                    logger.warn { "$platformName mongo: unknown collection name $collectionName" }
                }
            }

    abstract suspend fun consume(marketDataFlow: MarketDataFlow): Unit
}
