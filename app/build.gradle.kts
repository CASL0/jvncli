import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {
    jvmToolchain(21)

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun { mainClass.set("com.github.casl0.jvncli.MainKt") }
    }

    linuxX64()
    macosArm64()
    mingwX64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries { executable { entryPoint = "com.github.casl0.jvncli.main" } }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
        }
        // HTTP エンジンの選択は実行環境 (app) の責務。CIO 非対応の Windows のみ WinHttp を使う。
        jvmMain.dependencies { implementation(libs.ktor.client.cio) }
        linuxX64Main.dependencies { implementation(libs.ktor.client.cio) }
        macosArm64Main.dependencies { implementation(libs.ktor.client.cio) }
        mingwX64Main.dependencies { implementation(libs.ktor.client.winhttp) }
    }
}
