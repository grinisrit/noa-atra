package com.grinisrit.crypto.common.models

sealed class OrdersArray(
    val prices: LongArray,
    val amounts: LongArray,
) {
    val size = prices.size

    private fun LongArray.insert(index: Int, value: Long) = LongArray(size + 1) { i ->
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

    abstract fun findLevelIndex(level: Long): Int

    protected fun updateArrays(price: Long, amount: Long): Pair<LongArray, LongArray> {
        val index = findLevelIndex(price)

        if (prices[index] == price) {
            return if (amount == 0L) {
                Pair(
                    prices.erase(index),
                    amounts.erase(index)
                )
            } else {
                Pair(
                    prices.copyOf(),
                    amounts.update(index, amount)
                )
            }
        }
        return Pair(
            prices.insert(index, price),
            amounts.insert(index, amount)
        )
    }

    fun getCost(amount: Long): Long {

        var rest = amount
        var cost = 0L

        for (i in 0 until size) {
            if (amounts[i] > rest) {
                cost += rest * prices[i]
                rest = 0L
                break
            }
            cost += amounts[i] * prices[i]
            rest -= amounts[i]
        }


        // TODO()
        if (rest != 0L){
            return -100L
        }

        return cost
    }
}

class AsksArray(
    prices: LongArray,
    amounts: LongArray,
) : OrdersArray(prices, amounts) {

    fun update(price: Long, amount: Long): AsksArray = with(updateArrays(price, amount)) {
        AsksArray(first, second)
    }

    override fun findLevelIndex(level: Long): Int {
        for (i in 0 until size) {
            if (prices[i] >= level) {
                return i
            }
        }
        return size
    }
}

class BidsArray(
    prices: LongArray,
    amounts: LongArray,
) : OrdersArray(prices, amounts) {

    fun update(price: Long, amount: Long) = with(updateArrays(price, amount)) {
        BidsArray(first, second)
    }

    override fun findLevelIndex(level: Long): Int {
        for (i in 0 until size) {
            if (prices[i] <= level) {
                return i
            }
        }
        return size
    }
}
