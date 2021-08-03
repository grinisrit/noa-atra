package com.grinisrit.crypto.common.models

sealed class OrdersArray(
    val prices: FloatArray,
    val amounts: FloatArray,
) {
    abstract val isInvalid: Boolean

    val size = prices.size

    private fun FloatArray.insert(index: Int, value: Float) = FloatArray(size + 1) { i ->
        if (i < index) {
            this[i]
        } else if (i == index) {
            value
        } else {
            this[i - 1]
        }
    }

    private fun FloatArray.update(index: Int, value: Float) = FloatArray(size) { i ->
        if (i != index) {
            this[i]
        } else {
            value
        }
    }

    private fun FloatArray.erase(index: Int) = FloatArray(size - 1) { i ->
        if (i < index) {
            this[i]
        } else {
            this[i + 1]
        }
    }

    abstract fun findLevelIndex(level: Float): Int

    protected fun updateArrays(price: Float, amount: Float): Pair<FloatArray, FloatArray> {
        val index = findLevelIndex(price)

        if (prices[index] == price) {
            return if (amount == 0.0F) {
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

    fun getCost(amount: Float): Float? {

        var rest = amount
        var cost = 0.0F

        for (i in 0 until size) {
            if (amounts[i] > rest) {
                cost += rest * prices[i]
                rest = 0.0F
                break
            }
            cost += amounts[i] * prices[i]
            rest -= amounts[i]
        }


        // TODO()
        if (rest > 0.00001F) {
            return null
        }

        return cost
    }
}

class AsksArray(
    prices: FloatArray,
    amounts: FloatArray,
) : OrdersArray(prices, amounts) {

    fun update(price: Float, amount: Float): AsksArray = with(updateArrays(price, amount)) {
        AsksArray(first, second)
    }

    override val isInvalid: Boolean
        get() {
            for (i in 1 until size) {
                if (prices[i] < prices[i - 1]) {
                    return true
                }
            }
            return false
        }

    override fun findLevelIndex(level: Float): Int {
        for (i in 0 until size) {
            if (prices[i] >= level) {
                return i
            }
        }
        return size
    }
}

class BidsArray(
    prices: FloatArray,
    amounts: FloatArray,
) : OrdersArray(prices, amounts) {

    override val isInvalid: Boolean
        get() {
            for (i in 1 until size) {
                if (prices[i] > prices[i - 1]) {
                    return true
                }
            }
            return false
        }

    fun update(price: Float, amount: Float) = with(updateArrays(price, amount)) {
        BidsArray(first, second)
    }

    override fun findLevelIndex(level: Float): Int {
        for (i in 0 until size) {
            if (prices[i] <= level) {
                return i
            }
        }
        return size
    }
}
