# Analytics for Trading using NOA

In this repository we present a few examples for trading analytics that leverage 
the Bayesian computation platform [NOA](https://github.com/grinisrit/noa) 
and its experimental `kotlin-jvm` frontend 
within the [KMath](https://github.com/mipt-npm/kmath) library. 

## Installation 


To use `NOA`, you will need first to build & publish the [kmath-noa](https://github.com/grinisrit/kmath/tree/feature/noa/kmath-noa) 
module locally.

For data storage, you have to install 
[MongoDB](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/).
You can edit the configurations in the [conf.yaml](conf.yaml) file.

## High Frequency Trading in Cryptocurrency Markets

In this study, we explore the impact of High Frequency Trading 
on execution costs and liquidity for several 
Cryptocurrency exchanges.

You will find the data collection utilities 
within [crypto-hft-data](crypto-hft-analytics).
We get the LOB data feed from the following exchanges:

* [Coinbase](https://docs.pro.coinbase.com/#websocket-feed)
* [Kraken](https://docs.kraken.com/websockets/#message-book/)
* [Binance](https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md)
* [Deribit](https://docs.deribit.com/?python#subscriptions)

for the pairs: 
* USDBTC
* EURBTC 
* USDETH
* EURETH
* ETHBTC

Once you've collected a bit of data you can run the analysis with
the models we provide in [crypto-hft-analytics](crypto-hft-analytics). 

We are very grateful to contributions from:
* [Roland Grinis, GrinisRIT ltd.](https://github.com/grinisrit)
* [Andrei Kislitsin](https://github.com/AndreiKingsley)

We kindly acknowledge support from 
[JetBrains Research](https://research.jetbrains.org/) and
[Finery Markets](https://finerymarkets.com/).

(c) 2021 noa-atra-examples contributors