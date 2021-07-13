package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.CoinbasePlatform
import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

class CoinbaseMongoDBClient(platform: CoinbasePlatform, mongoDB: MongoDB) : MongoDBClient(platform, mongoDB) {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString<CoinbaseData>(data)
        val col = database.getCollection<DataTransport.DataTime<CoinbaseData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }
}
