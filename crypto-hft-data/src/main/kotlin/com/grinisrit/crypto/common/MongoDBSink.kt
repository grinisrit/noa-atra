package com.grinisrit.crypto.common

import com.grinisrit.crypto.*
import com.grinisrit.crypto.common.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

suspend fun getMongoDBServer(mongoConfig: MongoDBConfig): MongoDBServer {
    val mongo = KMongo.createClient(mongoConfig.address).coroutine
    logger.debug { "Connecting to MongoDB at ${mongoConfig.address} ..." }
    val dbs = mongo.listDatabaseNames()
    logger.debug { "Connection to MongoDB established, found ${dbs.size} databases" }
    return MongoDBServer(mongo)
}

class MongoDBServer internal constructor(val client: CoroutineClient)

abstract class MongoDBSink constructor(
    val server: MongoDBServer,
    val platformName: PlatformName,
    databaseNames: List<String>
) {
    private val database = server.client.getDatabase(platformName.toString())

    protected val nameToCollection = databaseNames.associateWith {
        database.getCollection<MarkedData>(it)
    }

    protected suspend inline fun<reified Data: PlatformData, reified Event: PlatformData>
            handleFlow(marketDataFlow: MarkedDataFlow) =
        marketDataFlow
            .filter { it is Data }
            .filter { it.platform_data !is Event }
            .collect {
                val col = nameToCollection[it.platform_data.type]
                col?.insertOne(it)
            }

    abstract suspend fun consume(marketDataFlow: MarkedDataFlow): Unit
}