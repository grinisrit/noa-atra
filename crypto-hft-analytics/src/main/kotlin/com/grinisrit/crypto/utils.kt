package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.Candles
import com.grinisrit.crypto.analysis.TimeWeightedValues
import space.kscience.kmath.noa.NoaDouble


/*
to read that in python do:
import torch
spreads = list(torch.jit.load(<spreadsPt>).parameters())[0]
 */
fun saveSpreads(
    spreadMetrics: Pair<TimeWeightedValues, Candles>,
    spreadsPt: String, dataRoot: String = "data"
): Unit {
    val (spreads, midprice) = spreadMetrics
    val n = midprice.timeList.size

    NoaDouble {
        val tensor = full(0.0, intArrayOf(6, n))
        tensor[0] = midprice.timeList.map { it.toDouble() }.toDoubleArray()
        tensor[1] = spreads.bidAsk.map { it.toDouble() }.toDoubleArray()
        tensor[2] = midprice.openList.map { it.toDouble() }.toDoubleArray()
        tensor[3] = midprice.highList.map { it.toDouble() }.toDoubleArray()
        tensor[4] = midprice.lowList.map { it.toDouble() }.toDoubleArray()
        tensor[5] = midprice.closeList.map { it.toDouble() }.toDoubleArray()
        tensor.save("${dataRoot}/${spreadsPt}")
    }!!
}
