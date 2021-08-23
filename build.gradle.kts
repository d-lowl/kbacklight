plugins {
    kotlin("multiplatform") version "1.5.20"
}

group = "space.dlowl"
version = "1.0-SNAPSHOT"
val korioVersion = "2.2.0"

repositories {
    maven(url = "https://dl.bintray.com/korlibs/korlibs")
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "space.dlowl.kbacklight.main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting

        nativeMain.dependencies {
            implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
        }
    }


}
