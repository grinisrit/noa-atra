package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.analysis.instantOfEpochMinute
import com.grinisrit.crypto.binance.BinanceMongoClient
import com.grinisrit.crypto.binance.BinanceRefinedDataPublisher
import com.grinisrit.crypto.bitstamp.BitstampMongoClient
import com.grinisrit.crypto.bitstamp.BitstampRefinedDataPublisher
import com.grinisrit.crypto.coinbase.CoinbaseMongoClient
import com.grinisrit.crypto.coinbase.CoinbaseRefinedDataPublisher
import com.grinisrit.crypto.common.bpMultiplier
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.deribit.*
import com.grinisrit.crypto.finery.*
import com.grinisrit.crypto.kraken.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.*


@OptIn(UnstablePlotlyAPI::class)
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amount = 10

    val server = config.mongodb.getMongoDBServer()

    val fineryMongo = FineryMongoClient(server)
    val coinbaseMongo = CoinbaseMongoClient(server)
    val binanceMongo = BinanceMongoClient(server)
    val krakenMongo = KrakenMongoClient(server)
    val bitstampMongo = BitstampMongoClient(server)

    val fineryOrderBookFlow = FineryRefinedDataPublisher.orderBookFlow(
        fineryMongo.loadSnapshots("BTC-USD").toList(),
        fineryMongo.loadUpdates("BTC-USD")
    )

    val coinbaseOrderBookFlow = CoinbaseRefinedDataPublisher.orderBookFlow(
        coinbaseMongo.loadSnapshots("BTC-USD").toList(),
        coinbaseMongo.loadUpdates("BTC-USD")
    )

    val binanceOrderBookFlow = BinanceRefinedDataPublisher.orderBookFlow(
        binanceMongo.loadSnapshots("BTCUSDT").toList(),
        binanceMongo.loadUpdates("BTCUSDT")
    )

    val krakenOrderBookFlow = KrakenRefinedDataPublisher.orderBookFlow(
        krakenMongo.loadSnapshots("XBT/USD").toList(),
        krakenMongo.loadUpdates("XBT/USD")
    )

    val bitstampOrderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(
        bitstampMongo.loadOrderBooks("btcusd")
    )

    val finerySpreadMetrics = countTimeWeightedMetricsAndLiquidity(fineryOrderBookFlow, listOf(amount))
    val coinbaseSpreadMetrics = countTimeWeightedMetricsAndLiquidity(coinbaseOrderBookFlow, listOf(amount))
    val binanceSpreadMetrics = countTimeWeightedMetricsAndLiquidity(binanceOrderBookFlow, listOf(amount))
    val krakenSpreadMetrics = countTimeWeightedMetricsAndLiquidity(krakenOrderBookFlow, listOf(amount))
    val bitstampSpreadMetrics = countTimeWeightedMetricsAndLiquidity(bitstampOrderBookFlow, listOf(amount))

    val fineryBAS = finerySpreadMetrics[amount]!!.first
    val coinbaseBAS = coinbaseSpreadMetrics[amount]!!.first
    val binanceBAS = binanceSpreadMetrics[amount]!!.first
    val krakenBAS = krakenSpreadMetrics[amount]!!.first
    val bitstampBAS = bitstampSpreadMetrics[amount]!!.first

    println(fineryBAS.bidAsk.sum() * bpMultiplier / fineryBAS.bidAsk.size)
    println(coinbaseBAS.bidAsk.sum() * bpMultiplier / coinbaseBAS.bidAsk.size)
    println(binanceBAS.bidAsk.sum() * bpMultiplier / binanceBAS.bidAsk.size)
    println(krakenBAS.bidAsk.sum() * bpMultiplier / krakenBAS.bidAsk.size)
    println(bitstampBAS.bidAsk.sum() * bpMultiplier / bitstampBAS.bidAsk.size)

    Plotly.plot {
        trace {
            x.set(fineryBAS.time.map { instantOfEpochMinute(it).toString() })
            y.set(fineryBAS.bidAsk.map { it * bpMultiplier })
            name = "Finery Markets"
        }
        trace {
            x.set(coinbaseBAS.time.map { instantOfEpochMinute(it).toString() })
            y.set(coinbaseBAS.bidAsk.map { it * bpMultiplier })
            name = "Coinbase"
        }
        trace {
            x.set(binanceBAS.time.map { instantOfEpochMinute(it).toString() })
            y.set(binanceBAS.bidAsk.map { it * bpMultiplier })
            name = "Binance"
        }
        trace {
            x.set(krakenBAS.time.map { instantOfEpochMinute(it).toString() })
            y.set(krakenBAS.bidAsk.map { it * bpMultiplier })
            name = "Kraken"
        }
        trace {
            x.set(bitstampBAS.time.map { instantOfEpochMinute(it).toString() })
            y.set(bitstampBAS.bidAsk.map { it * bpMultiplier })
            name = "Bitstamp"
        }
        layout {
            title = "Bid-ask spread comparison (10 BTC)"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Spread, base points"
            }
        }
    }.makeFile()
}