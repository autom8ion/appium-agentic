package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

/**
 * The product catalog, shown both on app launch (before login) and after a successful login —
 * confirmed against My Demo App Android 2.2.0: `MainActivity` launches straight into
 * `ProductCatalogFragment`, there's no separate pre-login landing screen.
 */
class AndroidProductsScreen(driver: AndroidDriver) : AndroidScreen(driver) {
    val title by element(AppiumBy.accessibilityId("title"))

    // Confirmed against MainActivity's header/drawer setup: the login flow isn't reachable
    // directly from the catalog screen, only via the hamburger menu drawer.
    private val menuButton by element(AppiumBy.accessibilityId("View menu"))
    private val loginMenuItem by element(AppiumBy.accessibilityId("Login Menu Item"))

    /** Opens the drawer menu and taps "Log In" to reach [AndroidLoginScreen]. */
    fun openLogin() {
        menuButton.click()
        loginMenuItem.click()
    }
}
