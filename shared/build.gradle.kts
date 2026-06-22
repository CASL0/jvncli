plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.metro)
}

kotlin {
    jvmToolchain(21)

    jvm()
    linuxX64()
    macosArm64()
    mingwX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktorfit.lib)
            implementation(libs.xmlutil.serialization)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
        }

        // CIO エンジンを共有する中間ソースセット（JVM / Linux / macOS）。
        // mingwX64 のみ CIO 非対応のため WinHttp を使う。
        val cioMain by creating { dependsOn(commonMain.get()) }
        cioMain.dependencies { implementation(libs.ktor.client.cio) }

        jvmMain.get().dependsOn(cioMain)
        linuxX64Main.get().dependsOn(cioMain)
        macosArm64Main.get().dependsOn(cioMain)

        mingwX64Main.dependencies { implementation(libs.ktor.client.winhttp) }
    }
}
