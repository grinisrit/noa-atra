package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*


@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>) {

    val config = loadConf(args)

    val basPoints1 = emptyPoints()
    val basPoints10 = emptyPoints()

    val assPoints1 = emptyPoints()
    val assPoints10 = emptyPoints()

    val bssPoints1 = emptyPoints()
    val bssPoints10 = emptyPoints()

    runBlocking {
        println(1)
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())
        println(2)
        val rawDataFlow = mongoClient.loadOrderBooks("btcusd")

        BitstampRefinedDataPublisher.orderBookFlow(rawDataFlow).collect { orderBook ->

            if (orderBook.isInvalid) {
                return@collect
            }

            val bas1 = orderBook.getBidAskSpread(1.0F)
            val bas10 = orderBook.getBidAskSpread(10.0F)

            val ass1 = orderBook.getAskSpread(1.0F)
            val ass10 = orderBook.getAskSpread(10.0F)

            val bss1 = orderBook.getBidSpread(1.0F)
            val bss10 = orderBook.getBidSpread(10.0F)

            val time = orderBook.timestamp

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