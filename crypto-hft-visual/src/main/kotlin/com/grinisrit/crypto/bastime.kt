package com.grinisrit.crypto

import com.grinisrit.crypto.bitstamp.*
import org.litote.kmongo.*
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.makeFile
import space.kscience.plotly.trace
import java.time.Instant

data class TimestampedMarketOrderBook(
    val receiving_datetime: Instant,
    val platform_data: OrderBook,
)

fun loadAllBookData(): List<OrderBook> {
    val mongo = KMongo.createClient("mongodb://localhost:27017")
    val col = mongo.getDatabase("bitstamp").getCollection<TimestampedMarketOrderBook>("order_book")

    val res = col.find(TimestampedMarketBook::platform_data / OrderBook::channel eq "detail_order_book_btcusd")

    return res.toList().map { it.platform_data }

}

fun getCost(amount: Float, data: List<OrderData>): Float {
    val size = data.size

    var rest = amount
    var cost = 0.0F

    for (i in 0 until size) {
        if (data[i].amount > rest) {
            cost += rest * data[i].price
            rest = 0.0F
            break
        }
        cost += data[i].amount * data[i].price
        rest -= data[i].amount
    }

    if (rest != 0.0F){
        return 0.0F
    }

    return cost
}

fun OrderBook.getBAS(amount: Float): Float {
    return (getCost(amount, data.asks) - getCost(amount, data.bids))/amount
}

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
