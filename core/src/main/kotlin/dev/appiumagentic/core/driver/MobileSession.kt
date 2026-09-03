package dev.appiumagentic.core.driver

import dev.appiumagentic.core.config.PlatformConfig
import io.appium.java_client.AppiumDriver

/** A live Appium session plus the resolved config it was started from. */
class MobileSession(
    val driver: AppiumDriver,
    val platform: Platform,
    val config: PlatformConfig,
) : AutoCloseable {
    val sessionId: String
        get() = driver.sessionId.toString()

    override fun close() {
        driver.quit()
    }
}
