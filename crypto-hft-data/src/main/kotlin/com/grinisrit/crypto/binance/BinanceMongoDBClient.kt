package com.grinisrit.crypto.binance

import com.grinisrit.crypto.BinancePlatform
import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

class BinanceMongoDBClient(platform: BinancePlatform, mongoDB: MongoDB) : MongoDBClient(platform, mongoDB) {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, BinanceDataSerializer)
        if (dataTime.data is Event){
            return
        }
        val col = database.getCollection<DataTransport.DataTime<BinanceData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }
}