package dev.appiumagentic.pageobject

import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By
import kotlin.properties.ReadOnlyProperty

/**
 * Base class for all page objects ("screens"). Subclasses declare their elements via the
 * [element]/[elements] delegates:
 *
 * ```
 * class AndroidLoginScreen(driver: AndroidDriver) : AndroidScreen(driver), LoginScreen {
 *     private val username by element(AppiumBy.accessibilityId("username-input"))
 *     override fun enterUsername(value: String) = username.sendKeys(value)
 * }
 * ```
 *
 * Prefer [io.appium.java_client.AppiumBy.accessibilityId] as the locator strategy; it maps to
 * `content-desc` on Android and `accessibilityIdentifier` on iOS, so it's the one strategy
 * that's stable across both platforms. Reach for a platform-specific strategy
 * (`androidUIAutomator`, `iOSClassChain`, `iOSNsPredicateString`) only when no accessibility
 * id is available, and say why in a comment next to the locator.
 */
abstract class Screen(protected val driver: AppiumDriver) {

    protected fun element(locator: By): ReadOnlyProperty<Screen, ScreenElement> =
        ReadOnlyProperty { _, _ -> ScreenElement(driver, locator) }

    protected fun elements(locator: By): ReadOnlyProperty<Screen, ScreenElementList> =
        ReadOnlyProperty { _, _ -> ScreenElementList(driver, locator) }
}
