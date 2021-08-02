package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.models.TimestampedData
import org.litote.kmongo.*
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.makeFile
import space.kscience.plotly.trace

/*data class TimestampedMarketOrderBook(
    val receiving_datetime: Instant,
    val platform_data: OrderBook,
)*/

typealias TimestampedMarketOrderBook = TimestampedData<OrderBook>

fun loadAllBookData(): List<OrderBook> {
    val mongo = KMongo.createClient("mongodb://localhost:27017")
    val col = mongo.getDatabase("bitstamp").getCollection<TimestampedMarketOrderBook>("order_book")

    val res = col.find(TimestampedMarketOrderBook::platform_data / OrderBook::channel eq "detail_order_book_btcusd")

    return res.toList().map { it.platform_data }

}

// TODO: Andrei use conf.yaml
fun main(){
    val btc1 = mutableListOf<Float>()
    val btc10 = mutableListOf<Float>()
    val time = mutableListOf<String>()

    loadAllBookData().forEach {
        btc1.add(it.getBAS(1.0F))
        btc10.add(it.getBAS(10.0F))
        time.add(it.datetime.toString())
    }

    val plot = Plotly.plot {
        trace {
            x.set(time)
            y.set(btc1)
            name = "1 BTC"
        }

        trace {
            x.set(time)
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
