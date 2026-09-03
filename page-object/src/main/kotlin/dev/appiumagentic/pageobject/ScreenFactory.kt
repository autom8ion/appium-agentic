package dev.appiumagentic.pageobject

import dev.appiumagentic.core.driver.Platform

/**
 * Picks the right platform implementation of a cross-platform screen interface, e.g.:
 *
 * ```
 * val login = ScreenFactory.forPlatform(session.platform,
 *     android = { AndroidLoginScreen(session.driver as AndroidDriver) },
 *     ios = { IosLoginScreen(session.driver as IOSDriver) },
 * )
 * ```
 */
object ScreenFactory {
    fun <T> forPlatform(platform: Platform, android: () -> T, ios: () -> T): T =
        when (platform) {
            Platform.ANDROID -> android()
            Platform.IOS -> ios()
        }
}
