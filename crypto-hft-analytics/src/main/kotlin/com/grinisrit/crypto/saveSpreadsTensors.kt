package com.grinisrit.crypto

import kotlinx.coroutines.coroutineScope

// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)
    saveFinerySpreads(config)
    saveKrakenSpreads(config)
    saveBitstampSpreads(config)
    saveBinanceSpreads(config)
    saveCoinbaseSpreads(config)

}