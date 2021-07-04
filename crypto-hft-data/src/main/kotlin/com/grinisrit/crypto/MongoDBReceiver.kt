package com.grinisrit.crypto

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ




class MongoDBReceiver(val channelName: String, mongoDB: MongoDB, zeroMQ: ZeroMQ) {

     val mapper = jacksonObjectMapper()

     val mongoDBAddress = "mongodb://${mongoDB.address}:${mongoDB.port}"

     val zeroMQAddress = "tcp://${zeroMQ.address}:${zeroMQ.port}"

    val zeroMQPattern = "(.+)///<>///(.+)".toRegex()

     fun getMessage(socketSUB: ZMQ.Socket) = flow {
        while (true) {
            val data = socketSUB.recvStr()
            emit(data)
        }
    }

    inline fun <reified T: CoinBaseInfo> mongoConnect() {
        val context = ZContext()
        val socketSUB = context.createSocket(SocketType.SUB)
        socketSUB.connect(zeroMQAddress)
        socketSUB.subscribe("{\"type\":\"$channelName\"")

        val client = KMongo.createClient(mongoDBAddress)
        val database = client.getDatabase("coinbase0407")
        val col = database.getCollection<MongoCBInfo<T>>(channelName)

        runBlocking {
            getMessage(socketSUB).collect {
                val (infoJson, dateTimeString) = zeroMQPattern.find(it)!!.destructured
                val cbInfo: T = mapper.readValue(infoJson)
                val dateTime = cbParse(dateTimeString)
                col.insertOne(MongoCBInfo(cbInfo, dateTime))
            }
        }
    }
}