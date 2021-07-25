package com.grinisrit.crypto

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory

private fun setUpLogger(): KLogger {
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger: Logger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    return KotlinLogging.logger { }
}

internal val logger = setUpLogger()
internal const val noMarketFlow = "No market data flow available"

