pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google() // Molecule/Mosaic が依存する AndroidX (compose-runtime 等) の配信元
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "jvncli"

include(":shared")

include(":app")
