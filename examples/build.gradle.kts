import java.net.URI

dependencies {
    implementation(project(":platform-android"))
    implementation(project(":platform-ios"))
    implementation(project(":test-support"))
    testImplementation(project(":platform-android"))
    testImplementation(project(":platform-ios"))
    testImplementation(project(":test-support"))
    runtimeOnly(libs.logback.classic)
}

// Sample-app coordinates for the "My Demo App" apps used by the example tests.
// Binaries are never committed; downloadSampleApps fetches pinned releases into
// examples/apps/ (gitignored). Swapping demo apps later is a config change here,
// not a code change in the framework.
val sampleAppsDir = layout.projectDirectory.dir("apps")
val androidAppUrl = "https://github.com/saucelabs/my-demo-app-android/releases/download/2.2.0/mda-2.2.0-25.apk"
val iosAppZipUrl = "https://github.com/saucelabs/my-demo-app-ios/releases/download/2.2.2/SauceLabs-Demo-App.Simulator.zip"

tasks.register("downloadSampleApps") {
    group = "appium-agentic"
    description = "Downloads the pinned My Demo App Android/iOS builds into examples/apps/ (gitignored)."

    val androidApk = sampleAppsDir.file("mda.apk").asFile
    val iosZip = sampleAppsDir.file("mda-ios.zip").asFile
    val iosUnzipDir = sampleAppsDir.dir("ios-payload").asFile
    // The release zip nests the bundle as Payload/My Demo App.app/; normalize to a stable,
    // space-free path so it can be referenced directly from config/ios/*.conf.
    val iosAppDir = sampleAppsDir.dir("MyDemoApp.app").asFile
    // Gradle pre-creates declared output directories before doLast runs, so iosAppDir itself
    // always "exists" by then — check for a file that only appears once the bundle is copied in.
    val iosAppMarker = File(iosAppDir, "Info.plist")

    outputs.file(androidApk)
    outputs.dir(iosAppDir)

    doLast {
        androidApk.parentFile.mkdirs()
        if (!androidApk.exists()) {
            logger.lifecycle("Downloading Android sample app from $androidAppUrl")
            URI(androidAppUrl).toURL().openStream().use { input ->
                androidApk.outputStream().use { output -> input.copyTo(output) }
            }
        }
        if (!iosAppMarker.exists()) {
            logger.lifecycle("Downloading iOS sample app from $iosAppZipUrl")
            URI(iosAppZipUrl).toURL().openStream().use { input ->
                iosZip.outputStream().use { output -> input.copyTo(output) }
            }
            copy {
                from(zipTree(iosZip))
                into(iosUnzipDir)
            }
            val extractedApp = iosUnzipDir.resolve("Payload").listFiles { f -> f.name.endsWith(".app") }
                ?.firstOrNull()
                ?: error("Could not find a .app bundle under Payload/ in $iosZip")
            extractedApp.copyRecursively(iosAppDir, overwrite = true)
            iosUnzipDir.deleteRecursively()
            iosZip.delete()
        }
    }
}

fun registerPlatformTestTask(name: String, packagePattern: String) =
    tasks.register<Test>(name) {
        group = "verification"
        description = "Runs the $name example tests against a running Appium server."
        dependsOn("downloadSampleApps")
        testClassesDirs = sourceSets.test.get().output.classesDirs
        classpath = sourceSets.test.get().runtimeClasspath
        filter {
            includeTestsMatching(packagePattern)
        }
        systemProperty("testEnv", System.getProperty("testEnv", "local"))
        systemProperty(
            "allure.results.directory",
            layout.buildDirectory.dir("allure-results").get().asFile.absolutePath,
        )
    }

registerPlatformTestTask("testAndroid", "dev.appiumagentic.examples.android.*")
registerPlatformTestTask("testIos", "dev.appiumagentic.examples.ios.*")

tasks.named<Test>("test") {
    dependsOn("downloadSampleApps")
    systemProperty(
        "allure.results.directory",
        layout.buildDirectory.dir("allure-results").get().asFile.absolutePath,
    )
}
