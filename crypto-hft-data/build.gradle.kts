plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.zeromq:jeromq:0.5.0")
    implementation("com.charleskorn.kaml:kaml:0.34.0")
    implementation("org.litote.kmongo:kmongo:4.2.8")

    implementation("io.ktor:ktor-server-core:1.6.0")
    implementation("io.ktor:ktor-server-netty:1.6.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-client-websockets:1.6.0")
    implementation("io.ktor:ktor-client-java:1.6.0")
}