package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.bitstamp.add
import com.grinisrit.crypto.bitstamp.emptyMinuteToValues
import com.grinisrit.crypto.bitstamp.ofEpochMicro
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.Plotly.plot
import space.kscience.plotly.makeFile
import space.kscience.plotly.trace


fun main(args: Array<String>) {

    val config = loadConf(args)
    val minuteToMidPrice = Pair(mutableListOf<String>(), mutableListOf<Float>())

    runBlocking {
        val mongoClient = CoinbaseMongoClient(config.mongodb.getMongoDBServer())

        val snapshotsList = mongoClient.loadSnapshots("BTC-USD").toList()
        val updatesFlow = mongoClient.loadUpdates("BTC-USD")



        launch {
            CoinbaseRefinedDataPublisher().orderBookFlow(snapshotsList, updatesFlow).collect { orderBook ->

                orderBook.getMidPrice().let {
                    minuteToMidPrice.first.add(ofEpochMicro(orderBook.timestamp).toString())
                    minuteToMidPrice.second.add(it)
                }

            }
        }

    }

    val plot = plot {
        trace {
            x.set(minuteToMidPrice.first)
            y.set(minuteToMidPrice.second)
        }
    }.makeFile()



}