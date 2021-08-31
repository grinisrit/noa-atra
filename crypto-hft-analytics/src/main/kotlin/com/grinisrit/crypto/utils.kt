package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.Candles
import com.grinisrit.crypto.analysis.TimeWeightedValues
import space.kscience.kmath.noa.NoaFloat
import space.kscience.kmath.noa.NoaLong

/*
to read that in python do:
import torch
bid_ask = list(torch.jit.load(<bidAskPt>).parameters())[0]
timestamps = list(torch.jit.load(<timePt>).parameters())[0]
 */

fun saveBidAskMetric(
    spreadMetrics:   Pair<TimeWeightedValues, Candles>,
    bidAskPt: String,
    timePt: String
): Unit {
    val (spreads, midprice) = spreadMetrics
    val n = midprice.timeList.size

    NoaFloat{
        val tensor = full(0f, intArrayOf(5,n))
        tensor[0] = spreads.bidAsk.toFloatArray()
        tensor[1] = midprice.openList.toFloatArray()
        tensor[2] = midprice.highList.toFloatArray()
        tensor[3] = midprice.lowList.toFloatArray()
        tensor[4] = midprice.closeList.toFloatArray()
        tensor.save(bidAskPt)
    }!!

    NoaLong {
        val tensor = copyFromArray(midprice.timeList.toLongArray(), intArrayOf(n))
        tensor.save(timePt)
    }!!
}