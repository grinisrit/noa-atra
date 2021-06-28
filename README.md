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
You can edit the configurations in [conf.yaml](conf.yaml) file.
## High Frequency Trading in Cryptocurrency markets

We get the LOB data feed from the following cryptocurrency exchanges:

* [Coinbase](https://docs.pro.coinbase.com/#websocket-feed)
* [Kraken](https://docs.kraken.com/websockets/#message-book/)
* [Binance](https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md)
* [Deribit](https://docs.deribit.com/?python#subscriptions)

