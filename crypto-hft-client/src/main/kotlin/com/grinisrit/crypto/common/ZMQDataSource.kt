package com.grinisrit.crypto.common

import com.grinisrit.crypto.ConfYAMl
import kotlinx.coroutines.CoroutineScope

fun CoroutineScope.createZMQDataSource(conf: ConfYAMl): ZMQDataSource =
    ZMQDataSource.fromConfig(this, conf)

class ZMQDataSource private constructor(
    private val marketDataBroker: MarketDataBroker
): MarketDataSharedSource {
    companion object {
        internal fun fromConfig(scope: CoroutineScope, conf: ConfYAMl): ZMQDataSource =
            ZMQDataSource(scope.createServer(conf))
    }
    override fun getFlow(): RawDataSharedFlow = marketDataBroker.getFlow()!! // TODO() ask is it ok
}
