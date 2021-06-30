package com.grinisrit.crypto

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.KMongo
import org.litote.kmongo.*
import org.litote.kmongo.getCollection
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ


class MongoDBThread(mongoDB: MongoDB, zeroMQ: ZeroMQ): Thread() {

    private val mongoDBAddress = "mongodb://${mongoDB.address}:${mongoDB.port}"

    private val zeroMQAddress = "tcp://${zeroMQ.address}:${zeroMQ.port}"

    private fun getMessage(socketSUB: ZMQ.Socket) = flow {
        while (true) {
            val data = socketSUB.recvStr()
            emit(data)
        }
    }

    override fun run() {
        val context = ZContext()
        val socketSUB = context.createSocket(SocketType.SUB)
        socketSUB.connect(zeroMQAddress)
        socketSUB.subscribe("")

        val client = KMongo.createClient(mongoDBAddress)
        val database = client.getDatabase("crypto-hft")
        val col = database.getCollection<CoinBaseChannelInfo>("coinbase")

        runBlocking {
            getMessage(socketSUB).collect {
                col.insertOne(it)
            }
        }
    }
}