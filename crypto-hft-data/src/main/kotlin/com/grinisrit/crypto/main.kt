package com.grinisrit.crypto

import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.bitstamp.*
import com.grinisrit.crypto.coinbase.createCoinbaseRequest
import com.grinisrit.crypto.coinbase.createCoinbaseSink
import com.grinisrit.crypto.coinbase.createCoinbaseSource
import com.grinisrit.crypto.common.createMarketDataBroker
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.deribit.createDeribitRequest
import com.grinisrit.crypto.deribit.createDeribitSink
import com.grinisrit.crypto.deribit.createDeribitSource

import com.grinisrit.crypto.kraken.createKrakenRequests
import com.grinisrit.crypto.kraken.createKrakenSink
import com.grinisrit.crypto.kraken.createKrakenSource
import kotlinx.cli.*
import kotlinx.coroutines.*
import java.io.File


fun main(args: Array<String>) {

    val cliParser = ArgParser("data")
    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()
    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"
    val config = parseConf(File(configPath).readText())



    runBlocking {

        val marketDataBroker = createMarketDataBroker(config)
        marketDataBroker.launchBroker()
        val marketDataFlow = marketDataBroker.getFlow()


        with(config.mongodb) {
            if (isOn) {

                val mongoServer = getMongoDBServer()

                val coinbaseSink = mongoServer.createCoinbaseSink()
                val binanceSink = mongoServer.createBinanceSink()
                val bitstampSink = mongoServer.createBitstampSink()
                val krakenSink = mongoServer.createKrakenSink()
                val deribitSink = mongoServer.createDeribitSink()

                marketDataFlow?.let { marketData ->
                    launch {
                        coinbaseSink.consume(marketData)
                    }
                    launch {
                        binanceSink.consume(marketData)
                    }
                    launch {
                        bitstampSink.consume(marketData)
                    }
                    launch {
                        krakenSink.consume(marketData)
                    }
                    launch {
                        deribitSink.consume(marketData)
                    }
                } ?: logger.warn { noMarketFlow }


            }
        }

        with(config.platforms.coinbase) {
            if (isOn) {
                val ws = createCoinbaseSource(createCoinbaseRequest())

                launch {
                    marketDataBroker.publishFlow(ws.getFlow())
                }
            }
        }

        with(config.platforms.binance) {
            if (isOn) {
                val ws = createBinanceSource(createBinanceRequest())
                val snapshots = createBinanceSnapshots()

                launch {
                    marketDataBroker.publishFlow(ws.getFlow())
                }
                marketDataFlow?.let { marketData ->
                    launch {
                        marketDataBroker.publishFlow(snapshots.getFlow(marketData))
                    }
                }?: logger.warn { noMarketFlow }
            }
        }

        with(config.platforms.bitstamp) {
            if (isOn) {
                val ws = createBitstampSource(createBitstampRequests())

                launch {
                    marketDataBroker.publishFlow(ws.getFlow())
                }
            }
        }

        with(config.platforms.kraken) {
            if (isOn) {
                val ws = createKrakenSource(createKrakenRequests())

                launch {
                    marketDataBroker.publishFlow(ws.getFlow())
                }
            }
        }

        with(config.platforms.deribit) {
            if (isOn) {
                val ws = createDeribitSource(createDeribitRequest())

                launch {
                    marketDataBroker.publishFlow(ws.getFlow())
                }
            }
        }

    }
}
