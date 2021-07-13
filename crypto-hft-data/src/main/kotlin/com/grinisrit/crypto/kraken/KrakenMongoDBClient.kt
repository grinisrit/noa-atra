package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.KrakenPlatform
import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

class KrakenMongoDBClient(platform: KrakenPlatform, mongoDB: MongoDB) : MongoDBClient(platform, mongoDB) {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, KrakenJsonParser())
        if (dataTime.data.channelName == "event") {
            return
        }
        val col = database.getCollection<DataTransport.DataTime<KrakenData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }
}