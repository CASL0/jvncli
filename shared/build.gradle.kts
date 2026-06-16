plugins { alias(libs.plugins.kotlinMultiplatform) }

kotlin {
    jvmToolchain(21)

    jvm()
    linuxX64()
    macosX64()
    macosArm64()
    mingwX64()

    sourceSets { commonMain.dependencies {} }
}
