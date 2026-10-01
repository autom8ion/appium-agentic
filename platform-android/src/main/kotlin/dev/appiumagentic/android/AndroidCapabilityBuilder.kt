package dev.appiumagentic.android

import dev.appiumagentic.core.config.PlatformConfig
import dev.appiumagentic.core.driver.CapabilityBuilder
import io.appium.java_client.android.options.UiAutomator2Options
import org.openqa.selenium.Capabilities
import java.time.Duration

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
            // Accept any activity of the app as "launched". Waiting for the launch activity
            // itself fails intermittently ("SplashActivity never started") when a splash screen
            // hands off to the main activity before UiAutomator2 polls — no wait duration fixes
            // that, it just waits longer for an activity that's already gone.
            .setAppWaitActivity("*")
            .setAppWaitDuration(Duration.ofSeconds(120))
}
