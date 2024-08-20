plugins {
    kotlin("js") version "1.8.0"
}

group = "me.s_limgeeee02"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    js {
        binaries.executable()
        browser {
            webpackTask {
                outputFileName = "whiteboard.js"
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
}