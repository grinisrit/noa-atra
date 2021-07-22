package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.*
import com.grinisrit.crypto.common.*
import org.litote.kmongo.coroutine.CoroutineClient


abstract class MongoDBHandler(
    val client: CoroutineClient,
    val platformName: PlatformName,
    databaseNames: List<String>,
) {
    private val database = client.getDatabase(platformName.toString())

    protected val nameToCollection = databaseNames.associateWith {
        database.getCollection<DataTime<ChannelData>>(it)
    }

    abstract suspend fun handleData(data: String)
}