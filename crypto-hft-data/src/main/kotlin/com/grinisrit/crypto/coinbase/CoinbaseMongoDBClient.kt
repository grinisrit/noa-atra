package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.ZeroMQ
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

class CoinbaseMongoDBClient(mongoDB: MongoDB, zeroMQ: ZeroMQ) : MongoDBClient("coinbase", mongoDB, zeroMQ) {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString<CoinbaseData>(data)
        val col = database.getCollection<DataTransport.DataTime<CoinbaseData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }
}