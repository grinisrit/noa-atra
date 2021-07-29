package com.grinisrit.crypto

import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.bitstamp.*
import com.grinisrit.crypto.coinbase.*
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.*
import com.grinisrit.crypto.deribit.*
import com.grinisrit.crypto.finery.*
import com.grinisrit.crypto.kraken.*
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
                val finerySink = mongoServer.createFinerySink()

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
                    launch {
                        finerySink.consume(marketData)
                    }
                } ?: commonLogger.warn { noMarketFlow }

                val mongoLogTimeout = 10000L

                launch {
                    while (isActive) {
                        coinbaseSink.sentinelLog()
                        delay(mongoLogTimeout)
                    }
                }

                launch {
                    while (isActive) {
                        binanceSink.sentinelLog()
                        delay(mongoLogTimeout)
                    }
                }

                launch {
                    while (isActive) {
                        bitstampSink.sentinelLog()
                        delay(mongoLogTimeout)
                    }
                }

                launch {
                    while (isActive) {
                        krakenSink.sentinelLog()
                        delay(mongoLogTimeout)
                    }
                }

                launch {
                    while (isActive) {
                        deribitSink.sentinelLog()
                        delay(mongoLogTimeout)
                    }
                }

                launch {
                    while (isActive) {
                        finerySink.sentinelLog()
                        delay(mongoLogTimeout)
                    }
                }

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
                } ?: commonLogger.warn { noMarketFlow }
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

        with(config.platforms.finery) {
            if (isOn) {
                val ws = createFinerySource(createFineryRequest())

                launch {
                    marketDataBroker.publishFlow(ws.getFlow())
                }

            }
        }

    }

}
