package com.grinisrit.crypto.common

import kotlin.math.min

class AskArray constructor(
    val prices: LongArray,
    val amounts: LongArray,
) {
    val size = prices.size

    init {
        assert(prices.size == amounts.size)
    }

    private fun LongArray.insert(index: Int, value: Long) = LongArray(size) { i ->
        if (i < index) {
            this[i]
        } else if (i == index) {
            value
        } else {
            this[i - 1]
        }
    }

    private fun LongArray.update(index: Int, value: Long) = LongArray(size) { i ->
        if (i != index) {
            this[i]
        } else {
            value
        }
    }

    private fun LongArray.erase(index: Int) = LongArray(size - 1) { i ->
        if (i < index) {
            this[i]
        } else {
            this[i + 1]
        }
    }

    private fun findLevelIndex(level: Long): Int {
        for (i in 0 until size) {
            if (prices[i] >= level) {
                return i
            }
        }
        return size
    }

    fun update(price: Long, amount: Long, maxSize: Int): AskArray {
        val index = findLevelIndex(price)

        if (index == size) {
            if (size + 1 > maxSize) {
                return AskArray(
                    prices.copyOf(),
                    amounts.copyOf()
                )
            }
        }
        if (prices[index] == price) {
            return if (amount == 0L) {
                AskArray(
                    prices.erase(index),
                    amounts.erase(index)
                )
            } else {
                AskArray(
                    prices.copyOf(),
                    amounts.update(index, amount)
                )
            }
        }
        return AskArray(
            prices.insert(index, price),
            amounts.insert(index, amount)
        )
    }

    fun update(prices: LongArray, amounts: LongArray): AskArray = AskArray(
        prices,
        amounts
    )

}