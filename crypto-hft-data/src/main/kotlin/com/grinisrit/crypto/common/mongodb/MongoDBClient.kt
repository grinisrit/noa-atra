package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.ZeroMQ
import com.grinisrit.crypto.coinbase.CoinbaseData
import com.grinisrit.crypto.common.DataTransport
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ


abstract class MongoDBClient(val platformName: String, mongoDB: MongoDB, zeroMQ: ZeroMQ) : Thread() {

    val mongoDBAddress = "mongodb://${mongoDB.address}:${mongoDB.port}"

    val zeroMQAddress = "tcp://${zeroMQ.address}:${zeroMQ.port}"

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
        socketSUB.connect(zeroMQAddress)
        socketSUB.subscribe(platformName)

        val client = KMongo.createClient(mongoDBAddress)
        val database = client.getDatabase(platformName)

        runBlocking {
            subscriptionFlow(socketSUB).collect { handleData(it, database) }
        }
    }

}
