package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.Platform
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.KMongo
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ


abstract class MongoDBClient(val platform: Platform, mongoDB: MongoDB) : Thread() {

    private val mongoDBAddress = "mongodb://${mongoDB.address}:${mongoDB.port}"

    private fun subscriptionFlow(socketSUB: ZMQ.Socket) = flow {
        while (true) {
            val data = socketSUB.recvStr()
            emit(data)
        }
    }

    abstract fun handleData(data: String, database: MongoDatabase)

    override fun run() {
        val context = ZContext()
        val socketSUB = context.createSocket(SocketType.SUB)
        socketSUB.connect(platform.zeromq_address)
        socketSUB.subscribe(platform.platformName)

        val client = KMongo.createClient(mongoDBAddress)
        val database = client.getDatabase(platform.platformName)

        runBlocking {
            subscriptionFlow(socketSUB).collect { handleData(it, database) }
        }
    }

}
