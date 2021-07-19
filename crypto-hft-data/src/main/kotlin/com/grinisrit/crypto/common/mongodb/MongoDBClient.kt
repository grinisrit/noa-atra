package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.litote.kmongo.KMongo
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ


class MongoDBClient(private val socketSUB: ZMQ.Socket, mongoDB: MongoDB) {

    val client = KMongo.createClient(mongoDB.address)

    val platformNameToHandler: MutableMap<String, MongoDBHandler> = mutableMapOf()

    fun run() {
        while (true) {
            val data = socketSUB.recvStr() ?: continue
          //  println(data)
            val platformName = DataTransport.getPlatformName(data)
            val database = client.getDatabase(platformName)
            platformNameToHandler[platformName]?.handleData(data, database)
        }
    }

}
