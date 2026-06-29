plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.metro)
    alias(libs.plugins.kover)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(21)

    jvm()
    linuxX64()
    macosArm64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.xml)
            implementation(libs.ktorfit.lib)
            implementation(libs.xmlutil.serialization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.molecule.runtime)
            implementation(libs.mosaic.runtime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine) // Presenter テスト: moleculeFlow().test{} の Flow アサーション
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
