package dev.appiumagentic.ios

import dev.appiumagentic.core.config.PlatformConfig
import dev.appiumagentic.core.driver.CapabilityBuilder
import io.appium.java_client.ios.options.XCUITestOptions
import org.openqa.selenium.Capabilities

/** Builds [XCUITestOptions] capabilities for a local iOS Simulator session. */
object IosCapabilityBuilder : CapabilityBuilder {
    override fun build(config: PlatformConfig): Capabilities =
        XCUITestOptions()
            .setDeviceName(config.deviceName)
            .setPlatformVersion(config.platformVersion)
            .setApp(config.appPath)
            .setNewCommandTimeout(config.newCommandTimeout)
            .setNoReset(config.noReset)
}
