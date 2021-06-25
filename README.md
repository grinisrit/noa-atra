# NOA: Analytics for Trading 

In this repository we present a few examples for trading analytics that leverage the Bayesian computation platform 
[NOA](https://github.com/grinisrit/noa) and its experimental `kotlin-jvm` 
frontend within the [KMath](https://github.com/mipt-npm/kmath) library.

## Installation 

Currently, the native artifacts are built only for `Unix` systems 
and `CUDA` as far as `GPU` execution is concerned.
If you are on Windows, we recommend you to install 
everything on [WSL](https://docs.nvidia.com/cuda/wsl-user-guide/index.html).

For data storage you need to install 
[MongoDB](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/).

## Data acquisition pipeline

We get the trading data feed from some of the major cryptocurrency exchanges. 

