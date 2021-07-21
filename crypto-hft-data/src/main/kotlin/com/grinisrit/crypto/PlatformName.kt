package com.grinisrit.crypto

enum class PlatformName {
    BINANCE,
    BITSTAMP,
    COINBASE,
    DERIBIT,
    KRAKEN,
    ;

    override fun toString(): String {
        return super.toString().lowercase()
    }

}