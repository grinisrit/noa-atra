package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.makeFile
import space.kscience.plotly.trace
import java.time.Instant


fun main(args: Array<String>) {
    val config = loadConf(args)

    val btc1 = mutableListOf<Float>()
    val btc10 = mutableListOf<Float>()
    val time1 = mutableListOf<String>()
    val time10 = mutableListOf<String>()


    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

        val flow = mongoClient.getOrderBook("btcusd")

        launch {
            flow.collect {
                with(it.platform_data.toLocalOrderBook()) {
                    if (isInvalid) {
                        return@collect
                    }
                    val bac1 = getBAS(1.0F)
                    val bac10 = getBAS(10.0F)
                    val time = Instant.ofEpochMilli(timestamp).toString()
                    if (bac1 != null) {
                        btc1.add(bac1)
                        time1.add(time)
                    }
                    if (bac10 != null) {
                        btc10.add(bac10)
                        time10.add(time)
                    }
                }
            }
        }
    }

    val plot = Plotly.plot {
        trace {
            x.set(time1)
            y.set(btc1)
            name = "1 BTC"
        }

        trace {
            x.set(time10)
            y.set(btc10)
            name = "10 BTC"
        }



        layout {
            title = "Bitstamp BTC bid-ask spread"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Bid-ask spread, $/BTC"
            }
        }
    }

    plot.makeFile()
}
