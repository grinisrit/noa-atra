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
./gradlew -Dorg.gradle.java.home=/path/to/local/jdk -q :crypto-hft-data:run --args=/path/to/conf.yaml
```
You can configure it in [conf.yaml](conf.yaml) to get the LOB data feed from the following 
exchanges and liquidity aggregators:

* [Coinbase](https://docs.pro.coinbase.com/#websocket-feed)
* [Kraken](https://docs.kraken.com/websockets/#message-book)
* [Bitstamp](https://www.bitstamp.net/websocket/v2) 
* [Binance](https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md)
* [Deribit](https://docs.deribit.com/?python#subscriptions)
* [Finery Markets](https://faq.finerymarkets.com/api-specifications)

for any of the traded instruments available on the platforms.

Once you've collected a bit of data you can run the analysis with
the models we provide in [crypto-hft-analytics](crypto-hft-analytics). 

The module [crypto-hft-visual](crypto-hft-visual) 
provides the visualisation apps.


## Acknowledgements

The following research has been carried out using our platform:
* R. Grinis, A. Kislitsyn, I. Drozdov and K. Shulga *Are Cryptocurrency Markets 
Running Behind the Fed? A Significant Shift in Crypto Markets Microstructure* 
(October 20, 2021). Available at 
[SSRN](http://ssrn.com/abstract=3951812)

We kindly acknowledge support from
[Finery Markets](https://finerymarkets.com/), 
[GrinisRIT ltd.](https://www.grinisrit.com/) and
[JetBrains Research](https://research.jetbrains.org/).

## License

NOA-ATRA is licensed under the terms and conditions of the GNU General
Public License (GPL) version 3 or - at your option - any later
version. The GPL can be read [online](https://www.gnu.org/licenses/gpl-3.0.en.html), 
or see the full [LICENSE](LICENSE)

Please note that NOA-ATRA license does NOT feature a
template exception to the GNU General Public License. This means that
you must publish any source code which uses any of the NOA-ATRA library functionality 
if you want to redistribute your program to third parties. If
this is unacceptable to you, please [contact us](info@grinisrit.com) 
for a commercial license.

For support or consultancy services 
contact [GrinisRIT](https://www.grinisrit.com).

(c) 2022 GrinisRIT ltd. 