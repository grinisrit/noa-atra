package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.*
import com.grinisrit.crypto.common.*
import com.mongodb.client.*
import org.litote.kmongo.getCollection

abstract class MongoDBHandler(
    val client: MongoClient,
    val platformName: PlatformName,
    databaseNames: List<String>,
) {
    private val database = client.getDatabase(platformName.toString())

    protected val nameToCollection = databaseNames.associateWith {
        database.getCollection<DataTime<ChannelData>>(it)
    }

    abstract fun handleData(data: String)
}