package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.LoginScreen
import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver
import java.time.Duration

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
        // This app has no in-UI way to dismiss the keyboard: no `textFieldShouldReturn`, no
        // `keyboardDismissMode` on the scroll view (so no interactive drag-to-dismiss either —
        // that's opt-in per scroll view, not a system-wide gesture). Every touch-based
        // approach tried here — typed "\n", tapping the real Return key, a raw W3C swipe over
        // the keyboard, and even XCTest's native `.swipeDown()` via `mobile: swipe` targeted
        // at the keyboard element — left it up. And the login form's scroll view can only
        // scroll ~59px total (content height 612 vs. frame height 553), nowhere near enough to
        // lift the button above the keyboard's ~230px, so dismissing it isn't optional here.
        //
        // Briefly backgrounding and re-foregrounding the app dismisses the keyboard as an
        // OS-level side effect, independent of anything the app itself supports, and the
        // previously-focused field doesn't reclaim it on return since this app never
        // implements refocus-on-foreground.
        iosDriver.runAppInBackground(Duration.ofSeconds(1))
        println("DEBUG isKeyboardShown right after background/foreground: ${iosDriver.isKeyboardShown}")
        println("DEBUG page source right after background/foreground: ${iosDriver.pageSource}")
        iosDriver.findElement(loginButtonLocator).click()
    }
}
