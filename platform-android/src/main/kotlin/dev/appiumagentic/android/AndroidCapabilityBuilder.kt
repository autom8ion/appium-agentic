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
            // Default app-launch wait (20s) is occasionally too tight on loaded CI runners —
            // seen intermittently in CI as "SplashActivity never started" session-creation
            // failures on an otherwise-healthy emulator. NEW_SESSION (the default @ResetApp
            // strategy) plus no-reset=false means every test reinstalls the app fresh, adding
            // to that variance; 60s wasn't consistently enough either.
            .setAppWaitDuration(Duration.ofSeconds(120))
}
