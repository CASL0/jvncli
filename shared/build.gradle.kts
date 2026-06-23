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

    sourceSets {
        // HTTP エンジン (CIO/WinHttp) は実行環境の都合なので、選択は app モジュールに委ねる。
        // ここはエンジン非依存の純粋なコアライブラリとする。
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
    }
}
