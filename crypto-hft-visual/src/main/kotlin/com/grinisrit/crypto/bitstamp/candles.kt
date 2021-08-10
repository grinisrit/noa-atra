package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import space.kscience.plotly.*
import space.kscience.plotly.Plotly.plot
import space.kscience.plotly.models.CandleStick
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.makeFile



class CandlePoints {
    val timeList: MutableList<String> = mutableListOf()
    val openList: MutableList<Float> = mutableListOf()
    val closeList: MutableList<Float> = mutableListOf()
    val lowList: MutableList<Float> = mutableListOf()
    val highList: MutableList<Float> = mutableListOf()
}


fun MinuteToValues.getCandlePoints(): CandlePoints {
    val candlePoints = CandlePoints()
    var close = values.first().first().second!! // TODO()
    forEach { (minute, values) ->
        val open = close
        close = values.last().second!!
        val low = values.minOf { it.second!! }
        val high = values.maxOf { it.second!! }

        with(candlePoints) {
            timeList.add(ofEpochMinute(minute).toString())
            openList.add(open)
            closeList.add(close)
            lowList.add(low)
            highList.add(high)
        }
    }
    return candlePoints
}


fun main(args: Array<String>) {

    val config = loadConf(args)

    val minuteToMidPrice = emptyMinuteToValues()

    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

        val orderBookFlow = mongoClient.loadOrderBooks("btcusd")

        launch {
            BitstampRefinedDataPublisher().orderBookFlow(orderBookFlow).collect { orderBook ->

                if (orderBook.isInvalid) {
                    return@collect
                }

                orderBook.getMidPrice().let {
                    minuteToMidPrice.add(orderBook.timestamp, it)
                }

            }
        }

    }

    val pointsMP = minuteToMidPrice.getCandlePoints()

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