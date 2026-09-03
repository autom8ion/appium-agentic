package dev.appiumagentic.core.driver

import dev.appiumagentic.core.config.PlatformConfig
import dev.appiumagentic.core.config.TestEnvironment
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import org.slf4j.LoggerFactory
import java.net.URI

/**
 * Builds and starts [MobileSession]s. Injected with one [CapabilityBuilder] per [Platform] so
 * it never has to know about `UiAutomator2Options`/`XCUITestOptions` directly.
 */
class MobileDriverFactory(private val capabilityBuilders: Map<Platform, CapabilityBuilder>) {
    private val log = LoggerFactory.getLogger(MobileDriverFactory::class.java)

    @JvmOverloads
    fun create(platform: Platform, environment: TestEnvironment = TestEnvironment.resolve()): MobileSession {
        val config = PlatformConfig.load(platform, environment)
        val builder = capabilityBuilders[platform]
            ?: error("No CapabilityBuilder registered for platform $platform")
        val capabilities = builder.build(config)
        val serverUrl = URI(config.appiumServerUrl).toURL()

        log.info(
            "Starting {} session against {} (device={}, platformVersion={}, app={})",
            platform,
            serverUrl,
            config.deviceName,
            config.platformVersion,
            config.appPath,
        )

        val driver = when (platform) {
            Platform.ANDROID -> AndroidDriver(serverUrl, capabilities)
            Platform.IOS -> IOSDriver(serverUrl, capabilities)
        }
        return MobileSession(driver, platform, config)
    }
}
