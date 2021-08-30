package com.grinisrit.crypto

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
    spreadMetrics: TimeWeightedValues,
    bidAskPt: String,
    timePt: String
): Unit {

    NoaFloat{
        val tensor = copyFromArray(spreadMetrics.bidAsk.toFloatArray(), intArrayOf(spreadMetrics.bidAsk.size))
        tensor.save(bidAskPt)
    }!!

    NoaLong {
        val tensor = copyFromArray(spreadMetrics.time.toLongArray(), intArrayOf(spreadMetrics.bidAsk.size))
        tensor.save(timePt)
    }!!
}