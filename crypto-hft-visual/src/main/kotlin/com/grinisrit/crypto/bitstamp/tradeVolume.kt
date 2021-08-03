package com.grinisrit.crypto.bitstamp

import org.litote.kmongo.KMongo
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import space.kscience.plotly.*
import space.kscience.plotly.models.ScatterMode

import java.time.Instant

/*
data class TimestampedMarketTrade(
    val receiving_datetime: Instant,
    val platform_data: Trade,
)

fun getTotalAmount(data: List<OrderData>): Float {
    return data.map { it.amount }.sum()
}

fun OrderBook.getTotalAmounts(): Pair<Float, Float> {
    return Pair(getTotalAmount(data.asks), getTotalAmount(data.bids))
}

fun loadAllTrades(): List<Trade> {
    val mongo = KMongo.createClient("mongodb://localhost:27017")
    val col = mongo.getDatabase("bitstamp").getCollection<TimestampedTrade>("trade")

    val res = col.find(TimestampedTrade::platform_data / Trade::channel eq "live_trades_btcusd")

    return res.toList().map { it.platform_data }

}

fun main(){
    val bidTotals = mutableListOf<Float>()
    val asksTotals = mutableListOf<Float>()
    val timeBook = mutableListOf<String>()

    val tradeAmounts = mutableListOf<Float>()
    val timeTrade = mutableListOf<String>()
/*
    loadAllBookData().forEach {
        with(it.getTotalAmounts()) {
            asksTotals.add(first)
            bidTotals.add(second)
        }
        timeBook.add(it.datetime.toString())
    }

 */

    loadAllTrades().forEach {
        tradeAmounts.add(it.data.amount)
        timeTrade.add(it.datetime.toString())
    }

    val plot = Plotly.plot {
        trace {
            x.set(timeTrade)
            y.set(asksTotals)
            name = "Asks total amount"
        }

        trace {
            x.set(timeTrade)
            y.set(bidTotals)
            name = "Bids total amount"
        }

        layout {
            title = "Bitstamp BTC bids and asks total amounts"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Amount"
            }
        }
    }

    val plot2 = Plotly.plot {


        scatter {
            x.set(timeTrade)
            y.set(tradeAmounts)
            name = "Trades"
            mode = ScatterMode.markers
        }


        layout {
            title = "Bitstamp BTC trades amounts"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Amount"
            }
        }
    }

    plot.makeFile()
    plot2.makeFile()
}

 */