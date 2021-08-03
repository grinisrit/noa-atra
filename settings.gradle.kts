pluginManagement {
    val kotlinVersion = "1.5.20"

    plugins {
        kotlin("jvm") version kotlinVersion
    }
}

rootProject.name = "noa-atra-examples"

include(":crypto-hft-data")
include(":crypto-hft-visual")
include(":crypto-hft-client")

if(System.getProperty("os.name") == "Linux"){
    include(":crypto-hft-analytics")
}