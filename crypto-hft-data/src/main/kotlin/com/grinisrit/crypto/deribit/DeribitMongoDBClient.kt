package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.DeribitPlatform
import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

class DeribitMongoDBClient(platform: DeribitPlatform, mongoDB: MongoDB) : MongoDBClient(platform, mongoDB) {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, DeribitDataSerializer)
        val col = database.getCollection<DataTransport.DataTime<DeribitData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }
}