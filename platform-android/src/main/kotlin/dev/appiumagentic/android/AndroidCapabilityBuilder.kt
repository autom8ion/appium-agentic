package dev.appiumagentic.android

import dev.appiumagentic.core.config.PlatformConfig
import dev.appiumagentic.core.driver.CapabilityBuilder
import io.appium.java_client.android.options.UiAutomator2Options
import org.openqa.selenium.Capabilities

/** Builds [UiAutomator2Options] capabilities for a local Android Emulator session. */
object AndroidCapabilityBuilder : CapabilityBuilder {
    override fun build(config: PlatformConfig): Capabilities =
        UiAutomator2Options()
            .setDeviceName(config.deviceName)
            .setPlatformVersion(config.platformVersion)
            .setApp(config.appPath)
            .setNewCommandTimeout(config.newCommandTimeout)
            .setNoReset(config.noReset)
            .setAutoGrantPermissions(true)
}
