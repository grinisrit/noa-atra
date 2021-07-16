package com.grinisrit.crypto.common

interface RequestBuilder {
    fun buildRequest(symbols: List<String>): List<String>
}