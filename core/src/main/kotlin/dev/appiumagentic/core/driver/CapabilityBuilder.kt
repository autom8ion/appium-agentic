package dev.appiumagentic.core.driver

import dev.appiumagentic.core.config.PlatformConfig
import org.openqa.selenium.Capabilities

/**
 * Builds platform-specific [Capabilities] (`UiAutomator2Options`/`XCUITestOptions`) from a
 * resolved [PlatformConfig]. Implementations live in `platform-android`/`platform-ios` so
 * `core` stays free of any single platform's Appium driver dependency; [MobileDriverFactory]
 * is wired with one builder per [Platform] by its caller.
 */
fun interface CapabilityBuilder {
    fun build(config: PlatformConfig): Capabilities
}
