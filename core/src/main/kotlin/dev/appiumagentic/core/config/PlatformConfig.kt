package dev.appiumagentic.core.config

import com.typesafe.config.ConfigFactory
import dev.appiumagentic.core.driver.Platform
import java.io.File
import java.time.Duration

/**
 * Resolved capability/session inputs for one (platform, environment) pair.
 *
 * Layering (lowest to highest precedence, all handled by Typesafe Config's [ConfigFactory.load]):
 * framework defaults in `core`'s `reference.conf` -> `config/<platform>/<env>.conf` on the
 * consuming module's test classpath (e.g. `examples/src/test/resources/...`) -> `-D` system
 * properties.
 */
data class PlatformConfig(
    val platform: Platform,
    val deviceName: String,
    val platformVersion: String,
    val appPath: String,
    /** Android package name / iOS bundle id, used by test-support's terminate+activate reset strategy. */
    val appId: String,
    val appiumServerUrl: String,
    val newCommandTimeout: Duration,
    val noReset: Boolean,
) {
    companion object {
        private const val NAMESPACE = "appium-agentic"

        @JvmStatic
        fun load(platform: Platform, environment: TestEnvironment): PlatformConfig {
            val resourceBase = "config/${platform.configName}/${environment.configName}"
            val config = ConfigFactory.load(resourceBase).getConfig(NAMESPACE)
            return PlatformConfig(
                platform = platform,
                deviceName = config.getString("device-name"),
                platformVersion = config.getString("platform-version"),
                appPath = File(config.getString("app-path")).absoluteFile.path,
                appId = config.getString("app-id"),
                appiumServerUrl = config.getString("appium-server-url"),
                newCommandTimeout = Duration.ofSeconds(config.getLong("new-command-timeout-seconds")),
                noReset = config.getBoolean("no-reset"),
            )
        }
    }
}
