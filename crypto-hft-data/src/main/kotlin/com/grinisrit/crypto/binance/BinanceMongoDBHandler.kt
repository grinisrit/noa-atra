package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

object BinanceMongoDBHandler : MongoDBHandler {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, BinanceDataSerializer)
        if (dataTime.data is Event){
            return
        }
        val col = database.getCollection<DataTransport.DataTime<BinanceData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }

}