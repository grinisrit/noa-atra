# Analytics for Trading using NOA

In this repository we present a few examples for trading analytics that leverage 
the nonlinear optimisation platform [NOA](https://github.com/grinisrit/noa) 
and its experimental `kotlin-jvm` frontend 
within the [KMath](https://github.com/mipt-npm/kmath) library. 

## Installation 

To use [kmath-noa](https://github.com/mipt-npm/kmath/tree/feature/noa/kmath-noa), 
you will need first to build & publish the module locally.

For data storage, you have to install 
[MongoDB](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/).
You can edit the configurations in the [conf.yaml](conf.yaml) file.

## High Frequency Trading in Cryptocurrency Markets

In this study, we explore the impact of High Frequency Trading 
on execution costs and liquidity for several 
Cryptocurrency exchanges.

You will find the data collection utilities 
within [crypto-hft-data](crypto-hft-analytics). To run the app you need to simply
execute:
```
$ ./gradlew -Dorg.gradle.java.home=/path/to/local/jdk -q :crypto-hft-data:run --args=/path/to/conf.yaml
```
You can configure it in [conf.yaml](conf.yaml) to get the LOB data feed from the following 
exchanges and liquidity aggregators:

* [Coinbase](https://docs.pro.coinbase.com/#websocket-feed)
* [Kraken](https://docs.kraken.com/websockets/#message-book/)
* [Bitstamp](https://www.bitstamp.net/websocket/v2/) 
* [Binance](https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md)
* [Deribit](https://docs.deribit.com/?python#subscriptions)
* [Finery Markets](https://faq.finerymarkets.com/api-specifications)

for any of the traded instruments available on the platforms.

Once you've collected a bit of data you can run the analysis with
the models we provide in [crypto-hft-analytics](crypto-hft-analytics). 

The module [crypto-hft-visual](crypto-hft-visual) 
provides the visualisation apps.


## Contributions

We are very grateful to contributions from:
* [Roland Grinis, GrinisRIT ltd.](https://github.com/grinisrit)
* [Andrei Kislitsin](https://github.com/AndreiKingsley)

We kindly acknowledge support from 
[JetBrains Research](https://research.jetbrains.org/) and
[Finery Markets](https://finerymarkets.com/).

(c) 2021 noa-atra-examples contributors