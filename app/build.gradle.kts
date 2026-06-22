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

    sourceSets { commonMain.dependencies { implementation(projects.shared) } }
}
