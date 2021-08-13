package com.grinisrit.crypto.common

import com.grinisrit.crypto.analysis.*
import space.kscience.plotly.*
import space.kscience.plotly.models.*
import kotlin.math.min

// TODO??
fun timeWeightedSpreadsPlot(
    amount: Float,
    timeWeightedSpreads: TimeWeightedSpreads,
    platformName: String
): Plot {
    val time = timeWeightedSpreads.time.map { instantOfEpochMinute(it).toString() }
    return Plotly.plot {
        bar {
            x.set(time)
            y.set(timeWeightedSpreads.bidAsk)
            name = "Bid-ask spread"
        }

        bar {
            x.set(time)
            y.set(timeWeightedSpreads.ask)
            name = "Ask spread"
        }

        bar {
            x.set(time)
            y.set(timeWeightedSpreads.bid)
            name = "Bid spread"
        }

        // TODO symbols
        layout {
            title = "$platformName time weighted spreads for $amount BTC"
            barmode = BarMode.group
            showlegend = true
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Spread, $/BTC"
            }
        }
    }
}

fun timeWeightedLiquidityPlot(
    amountToTimeWeightedSpreads: AmountToTimeWeightedSpreads,
    platformName: String,
): Plot {
    // TODO symbols
    val traces = amountToTimeWeightedSpreads.map { (amount, spreadData) ->
        Bar {
            x.set(spreadData.time.map { instantOfEpochMinute(it).toString() })
            y.set(spreadData.liquidity)
            name = "$amount BTC"
        }
    }
    return Plotly.plot {
        traces(traces)

        layout {
            title = "$platformName lack of liquidity"
            barmode = BarMode.group
            showlegend = true
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Fraction of a minute"
            }
        }
    }
}

fun timeWeightedTradesPlot(
    timeWeightedTradesAmountsData: TimeWeightedTradesAmountsData,
    platformName: String,
    lowerBound: Float? = null
): Plot {
    val time = timeWeightedTradesAmountsData.time.map {
        instantOfEpochMinute(it).toString()
    }
    // TODO symbols
    return Plotly.plot {
        bar {
            x.set(time)
            y.set(timeWeightedTradesAmountsData.buy.map {
                if (lowerBound == null) {
                    it
                } else {
                    min(it, lowerBound)
                }
            })
            name = "buy"
        }

        bar {
            x.set(time)
            y.set(timeWeightedTradesAmountsData.sell.map {
                if (lowerBound == null) {
                    it
                } else {
                    min(it, lowerBound)
                }
            })
            name = "sell"
        }

        layout {
            title = "$platformName trades amounts"
            barmode = BarMode.group
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Trade volume"
            }
        }

    }
}
