package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/**
 * The product catalog, shown both on app launch (before login) and after a successful login —
 * confirmed against My Demo App iOS 2.2.2: `Info.plist`'s `UIMainStoryboardFile` is `TabBar`,
 * there's no separate pre-login landing screen. `screen`'s identifier is confirmed from the
 * app's own XCUITest suite (`PageObject.swift`); `moreTab`/`loginButton` are confirmed against
 * `TabBar.storyboard`/`Menu.storyboard`'s `accessibilityConfiguration` identifiers.
 */
class IosCatalogScreen(driver: IOSDriver) : IosScreen(driver) {
    val screen by element(AppiumBy.accessibilityId("Catalog-screen"))

    // The login flow isn't reachable directly from the catalog screen, only via the "More"
    // tab's menu screen.
    private val moreTab by element(AppiumBy.accessibilityId("More-tab-item"))
    private val loginButton by element(AppiumBy.accessibilityId("Login Button"))

    /** Taps the "More" tab, then "Login", to reach [IosLoginScreen]. */
    fun openLogin() {
        moreTab.click()
        loginButton.click()
    }
}
