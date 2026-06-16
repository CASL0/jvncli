pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement { repositories { mavenCentral() } }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "jvncli"

include(":shared")

include(":app")
