package com.grinisrit.crypto.common

import com.grinisrit.crypto.analysis.AmountToTimeWeightedSpreads
import com.grinisrit.crypto.analysis.instantOfEpochMinute
import space.kscience.dataforge.meta.invoke
import space.kscience.plotly.Plot
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.models.CandleStick
import space.kscience.plotly.models.Trace

fun midPriceCandlestickPlot(
    amountToTimeWeightedSpreads: AmountToTimeWeightedSpreads,
    platformName: String
): Plot {
    val traces = amountToTimeWeightedSpreads.map { (amount, spreadData) ->
        val candles = spreadData.second
        CandleStick {
            x.strings = candles.timeList.map {  instantOfEpochMinute(it).toString()  }

            open.numbers = candles.openList.map { it / amount }
            close.numbers = candles.closeList.map { it / amount }

            low.numbers = candles.lowList.map { it / amount }
            high.numbers = candles.highList.map { it / amount }
            /*
            increasing {
                lineColor("#17BECF")
            }

            decreasing {
                lineColor("#7F7F7F")
            }

            line { color("rgba(31,119,180,1)") }

             */


            name = "$amount BTC"
        }
    }

    return Plotly.plot {
        traces(traces)

        layout {
            title = "$platformName mid prices"
            showlegend = true
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Price, $"
            }
        }
    }
}