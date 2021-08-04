package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*
import java.time.Instant
import java.time.temporal.ChronoUnit

fun ofEpochMicro(epochMicro: Long): Instant =
    Instant.EPOCH.plus(epochMicro, ChronoUnit.MICROS)

typealias MutablePoints = Pair<MutableList<String>, MutableList<Float>>

fun MutablePoints.add(time: String, data: Float){
    first.add(time)
    second.add(data)
}

fun getEmptyPoints() = Pair(mutableListOf<String>(), mutableListOf<Float>())

@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>) {

    val config = loadConf(args)

    val basPoints1 = getEmptyPoints()
    val basPoints10 = getEmptyPoints()

    val assPoints1 = getEmptyPoints()
    val assPoints10 = getEmptyPoints()

    val bssPoints1 = getEmptyPoints()
    val bssPoints10 = getEmptyPoints()

    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

        val rawDataFlow = mongoClient.getOrderBook("btcusd")

        BitstampRefinedDataPublisher().orderBookFlow(rawDataFlow).collect { orderBook ->

            if (orderBook.isInvalid) {
                return@collect
            }

            val bas1 = orderBook.getBidAskSpread(1.0F)
            val bas10 = orderBook.getBidAskSpread(10.0F)

            val ass1 = orderBook.getAskSideSpread(1.0F)
            val ass10 = orderBook.getAskSideSpread(10.0F)

            val bss1 = orderBook.getBidSideSpread(1.0F)
            val bss10 = orderBook.getBidSideSpread(10.0F)

            val time = ofEpochMicro(orderBook.timestamp).toString()

            bas1?.let {
                basPoints1.add(time, it)
            }

            bas10?.let {
                basPoints10.add(time, it)
            }

            ass1?.let {
                assPoints1.add(time, it)
            }

            ass10?.let {
                assPoints10.add(time, it)
            }

            bss1?.let {
                bssPoints1.add(time, it)
            }

            bss10?.let {
                bssPoints10.add(time, it)
            }

        }

    }

    val plot = Plotly.grid {
        plot {

            trace {
                x.set(basPoints1.first)
                y.set(basPoints1.second)
                name = "1 BTC"
            }

            trace {
                x.set(basPoints10.first)
                y.set(basPoints10.second)
                name = "10 BTC"
            }

            layout {
                title = "Bitstamp BTC bid-ask spread"
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Spread, $/BTC"
                }
            }
        }

        plot {

            trace {
                x.set(assPoints1.first)
                y.set(assPoints1.second)
                name = "1 BTC"
            }

            trace {
                x.set(assPoints10.first)
                y.set(assPoints10.second)
                name = "10 BTC"
            }

            layout {
                title = "Bitstamp BTC ask side spread"
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Spread, $/BTC"
                }
            }
        }

        plot {

            trace {
                x.set(bssPoints1.first)
                y.set(bssPoints1.second)
                name = "1 BTC"
            }

            trace {
                x.set(bssPoints10.first)
                y.set(bssPoints10.second)
                name = "10 BTC"
            }

            layout {
                title = "Bitstamp BTC bid side spread"
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Spread, $/BTC"
                }
            }
        }
    }

    plot.makeFile()

}