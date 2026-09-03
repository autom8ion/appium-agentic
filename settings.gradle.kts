plugins {
    // Lets Gradle auto-provision the JDK 21 toolchain (see root build.gradle.kts) when no
    // matching local JDK is on PATH, so the wrapper works out of the box on any JDK 8+.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "appium-agentic"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(
    "core",
    "page-object",
    "platform-android",
    "platform-ios",
    "test-support",
    "examples",
)
