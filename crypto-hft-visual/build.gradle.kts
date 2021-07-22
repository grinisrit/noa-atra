plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.5.20"
}

repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

dependencies {
    implementation(project(":crypto-hft-data"))

    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")

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
    implementation("space.kscience:plotlykt-server:0.4.2")
}