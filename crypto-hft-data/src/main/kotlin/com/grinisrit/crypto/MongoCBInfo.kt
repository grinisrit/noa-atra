package com.grinisrit.crypto

import java.util.*

data class MongoCBInfo<T: CoinBaseInfo>(
    val info: T,
    val receipt_date_time: Date,
)

