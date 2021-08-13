package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import space.kscience.plotly.Plotly.plot
import space.kscience.plotly.models.CandleStick
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.makeFile



fun main(args: Array<String>) {

    val config = loadConf(args)

    val minuteToMidPrice = emptyMinuteToValues()

    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

        val orderBookFlow = mongoClient.loadOrderBooks("btcusd")

        launch {
            BitstampRefinedDataPublisher.orderBookFlow(orderBookFlow).collect { orderBook ->

                if (orderBook.isInvalid) {
                    return@collect
                }

                orderBook.getMidPrice().let {
                    minuteToMidPrice.add(orderBook.timestamp, it)
                }

            }
        }

    }

    val pointsMP = minuteToMidPrice.toCandlePoints()

    println(pointsMP.lowList.size)

    val plot = plot {

        traces(


        CandleStick {
            x.strings = pointsMP.timeList

            open.numbers = pointsMP.openList
            close.numbers = pointsMP.closeList

            low.numbers = pointsMP.lowList
            high.numbers = pointsMP.highList

            increasing {
                lineColor("#17BECF")
            }

            decreasing {
                lineColor("#7F7F7F")
            }

            line { color("rgba(31,119,180,1)") }
        }
        )
    }

    plot.makeFile()


}