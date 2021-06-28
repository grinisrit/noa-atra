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


class MongoDBThread: Thread() {

    val dbURL = "localhost"
    val dbPORT = "27017"
    val connStr = "mongodb://" + dbURL + ":" + dbPORT

    private fun getMessage(socketSUB: ZMQ.Socket) = flow {
        while (true) {
            val data = socketSUB.recvStr()
            emit(data)
        }
    }

    override fun run() {
        val context = ZContext()
        val socketSUB = context.createSocket(SocketType.SUB)
        socketSUB.connect("tcp://localhost:5897")
        socketSUB.subscribe("")

        val client = KMongo.createClient(connStr)
        val database = client.getDatabase("coinbase")
        val colHB = database.getCollection<CoinBaseChannelInfo>("heartbeat")

        runBlocking {
            getMessage(socketSUB).collect {
                println(it)
                colHB.insertOne(it)
            }
        }
    }
}