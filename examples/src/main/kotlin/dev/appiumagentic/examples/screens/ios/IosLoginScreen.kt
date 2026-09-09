package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.LoginScreen
import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver
import org.openqa.selenium.NoSuchElementException

/**
 * Confirmed against My Demo App iOS 2.2.2's `LoginViewController.swift` and
 * `Authentication.storyboard`: neither the username nor password `UITextField` has an
 * `accessibilityIdentifier` assigned anywhere (the `userNameTF`/`passwordTF` IBOutlet names
 * are not exposed as accessibility ids — CI confirmed `accessibilityId("userNameTF")` finds
 * nothing), so accessibilityId isn't available for these two fields. Both are unique by
 * XCUITest element type on this screen — the password field is the only
 * `XCUIElementTypeSecureTextField` because of its `secureTextEntry` flag — so iOSClassChain is
 * the documented exception.
 */
class IosLoginScreen(driver: IOSDriver) : IosScreen(driver), LoginScreen {

    private val usernameField by element(AppiumBy.iOSClassChain("**/XCUIElementTypeTextField[1]"))
    private val passwordField by element(
        AppiumBy.iOSClassChain("**/XCUIElementTypeSecureTextField[1]"),
    )
    private val loginButtonLocator = AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == \"Login\"`]")

    override fun enterUsername(username: String) = usernameField.sendKeys(username)

    override fun enterPassword(password: String) = passwordField.sendKeys(password)

    override fun tapLogin() {
        // This app has no in-UI way to dismiss the keyboard (no `textFieldShouldReturn`, no
        // `keyboardDismissMode` on the scroll view). Every raw-coordinate approach tried here
        // (typed "\n", tapping the Return key directly, a raw W3C swipe over the keyboard's
        // own area) left it up — the login form's scroll view can only scroll ~59px total
        // (content height 612 vs. frame height 553), nowhere near enough to lift the button
        // above the keyboard's ~230px, so dismissing it isn't optional here. `mobile: swipe`
        // calls XCTest's own native `.swipeDown()` on a target element rather than
        // synthesizing a touch sequence, so swipe the keyboard element itself down — iOS's
        // system-wide "drag down on the keyboard to dismiss" gesture, called natively instead
        // of via raw coordinates.
        try {
            val keyboard = iosDriver.findElement(AppiumBy.className("XCUIElementTypeKeyboard"))
            iosDriver.executeScript("mobile: swipe", mapOf("element" to keyboard, "direction" to "down"))
        } catch (_: NoSuchElementException) {
            // Keyboard wasn't shown — nothing to dismiss.
        }
        iosDriver.findElement(loginButtonLocator).click()
    }
}
