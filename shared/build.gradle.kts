plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.metro)
    alias(libs.plugins.kover)
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
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.xml)
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

kover {
    reports {
        filters {
            excludes {
                // 自動生成コードは計測対象外にする。
                annotatedBy("kotlinx.serialization.Serializable") // @Serializable DTO の生成シリアライザ
                classes(
                    "com.github.casl0.jvncli.core.network._JvnApi*", // Ktorfit 生成の API 実装
                    "com.github.casl0.jvncli.core.di.JvnGraph*", // Metro 生成の DI グラフ
                    "*MetroFactory*", // Metro 生成のファクトリ
                )
            }
        }
        verify {
            rule {
                // 行カバレッジの下限。check タスクで検証される。
                minBound(80)
            }
        }
    }
}
