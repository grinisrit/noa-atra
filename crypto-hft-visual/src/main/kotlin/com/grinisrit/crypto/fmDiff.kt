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
import com.grinisrit.crypto.finery.FineryMongoClient
import com.grinisrit.crypto.finery.FineryRefinedDataPublisher
import com.grinisrit.crypto.kraken.KrakenMongoClient
import com.grinisrit.crypto.kraken.KrakenRefinedDataPublisher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import space.kscience.plotly.*


fun countDiff(base: Map<Long, Float>, other: Map<Long, Float>): Pair<List<Long>, List<Float>> {
    val times = mutableListOf<Long>()
    val values = mutableListOf<Float>()

    for ((time, value) in base) {
        other[time]?.let {
            times.add(time)
            values.add(value - it)
        }
    }

    return times to values
}

@OptIn(UnstablePlotlyAPI::class)
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amount = 10.0F

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

    val fineryTimeToBAS = fineryBAS.time.zip(fineryBAS.bidAsk).toMap()
    val coinbaseTimeToBAS = coinbaseBAS.time.zip(coinbaseBAS.bidAsk).toMap()
    val binanceTimeToBAS = binanceBAS.time.zip(binanceBAS.bidAsk).toMap()
    val krakenTimeToBAS = krakenBAS.time.zip(krakenBAS.bidAsk).toMap()
    val bitstampTimeToBAS = bitstampBAS.time.zip(bitstampBAS.bidAsk).toMap()

    val coinbaseStats = countDiff(fineryTimeToBAS, coinbaseTimeToBAS)
    val binanceStats = countDiff(fineryTimeToBAS, binanceTimeToBAS)
    val krakenStats = countDiff(fineryTimeToBAS, krakenTimeToBAS)
    val bitstampStats = countDiff(fineryTimeToBAS, bitstampTimeToBAS)

    Plotly.plot {
        trace {
            x.set(coinbaseStats.first.map { instantOfEpochMinute(it).toString() })
            y.set(coinbaseStats.second.map { it * bpMultiplier })
           // name = "Coinbase"
        }
        layout {
            title = "Finery Markets and Coinbase bid-ask spread (10 BTC) difference"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Spread difference, base points"
            }
        }
    }.makeFile()

    Plotly.plot {
        trace {
            x.set(binanceStats.first.map { instantOfEpochMinute(it).toString() })
            y.set(binanceStats.second.map { it * bpMultiplier })
            // name = "Coinbase"
        }
        layout {
            title = "Finery Markets and Binance bid-ask spread (10 BTC) difference"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Spread difference, base points"
            }
        }
    }.makeFile()

    Plotly.plot {
        trace {
            x.set(krakenStats.first.map { instantOfEpochMinute(it).toString() })
            y.set(krakenStats.second.map { it * bpMultiplier })
            // name = "Coinbase"
        }
        layout {
            title = "Finery Markets and Kraken bid-ask spread (10 BTC) difference"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Spread difference, base points"
            }
        }
    }.makeFile()

    Plotly.plot {
        trace {
            x.set(bitstampStats.first.map { instantOfEpochMinute(it).toString() })
            y.set(bitstampStats.second.map { it * bpMultiplier })
            // name = "Coinbase"
        }
        layout {
            title = "Finery Markets and Bitstamp bid-ask spread (10 BTC) difference"
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Spread difference, base points"
            }
        }
    }.makeFile()
}