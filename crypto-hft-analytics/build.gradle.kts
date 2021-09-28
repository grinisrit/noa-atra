plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

    implementation(project(":crypto-hft-data"))
    implementation("space.kscience:kmath-noa:0.3.0-dev-14")
    implementation("space.kscience:kmath-tensors:0.3.0-dev-14")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    
    testImplementation(kotlin("test"))
}

val home: String = System.getProperty("user.home")
val jNoaLocation: String = "$home/.konan/third-party/noa-v0.0.1/cpp-build/jnoa"

tasks {
    withType<Test>{
        systemProperty("java.library.path", jNoaLocation)
    }
}
