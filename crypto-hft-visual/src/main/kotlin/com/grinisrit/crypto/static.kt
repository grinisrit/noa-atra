package com.grinisrit.crypto


import com.grinisrit.crypto.deribit.*
import org.litote.kmongo.*
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.Plotly
import space.kscience.plotly.UnstablePlotlyAPI
import space.kscience.plotly.makeFile
import space.kscience.plotly.trace
import kotlin.math.*


import space.kscience.plotly.*
import space.kscience.plotly.models.*
import space.kscience.plotly.palettes.T10
import java.time.Instant

data class TimestampedMarketTrades(
    val receiving_datetime: Instant,
    val platform_data: Trades,
)

data class TimestampedMarketBook(
    val receiving_datetime: Instant,
    val platform_data: Book,
)

fun someData(): List<Pair<Instant, Float>> {
    val mongo = KMongo.createClient("mongodb://localhost:27017")

    val from = Instant.parse("2021-07-29T19:16:15.703160Z")
    val to = Instant.parse("2021-07-29T19:26:17.703160Z")

    println(from)
    println(to)

    val col = mongo.getDatabase("deribit").getCollection<TimestampedMarketTrades>("trades")

    val f = with(TimestampedMarketTrades::platform_data/ Trades::params / TradesParameters::data  / TradeData::datetime) {
        col.find(and(this gte from, this lte to)).flatMap {
            it.platform_data.params.data.filter { data ->
                data.instrument_name == "BTC-PERPETUAL"
            }.map { data ->
                Pair(data.datetime, data.price)
            }
        }
    }

    return f

}


fun someDataAsk(): List<Triple<Instant, Float, Float>> {
    val mongo = KMongo.createClient("mongodb://localhost:27017")

    val from = Instant.parse("2021-07-29T19:16:15.703160Z")
    val to = Instant.parse("2021-07-29T19:26:17.703160Z")

    println(from)
    println(to)

    val col = mongo.getDatabase("deribit").getCollection<TimestampedMarketBook>("book")

    val f = with(TimestampedMarketBook::platform_data/ Book::params / BookParameters::data  / BookData::datetime) {
        col.find(and(this gte from, this lte to)).toList().filter {
            it.platform_data.params.data.instrument_name == "BTC-PERPETUAL"
        }.map {
            with(it.platform_data.params.data) {
                Triple(datetime, asks.first().price, bids.first().price)
            }
        }
    }

    return f

}

fun main() {


    val data = someData()

    val xValuesTrade = data.map { it.first.toString() }
    val yValuesTrade = data.map { it.second }

    val dataBook = someDataAsk()

    val xTime = dataBook.map { it.first.toString() }
    val yAsk = dataBook.map { it.second }
    val yBid = dataBook.map { it.third }

    val plot = Plotly.plot {
        scatter {
            x.set(xValuesTrade)
            y.set(yValuesTrade)
            name = "Trades"
            mode = ScatterMode.markers
        }

        trace {
            x.set(xTime)
            y.set(yAsk)
            name = "Best Ask"
        }

        trace {
            x.set(xTime)
            y.set(yBid)
            name = "Best Bid"
        }

        layout {
            title = "Deribit BTC info"
            xaxis {
                title = "Time"
            }
            yaxis {
                title = "$"
            }
        }
    }

    plot.makeFile()


}

