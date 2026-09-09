package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.LoginScreen
import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver
import org.openqa.selenium.interactions.PointerInput
import org.openqa.selenium.interactions.Sequence
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

    // A trailing "\n" simulates tapping the keyboard's Return key (documented XCTest
    // `typeText:` behavior), dismissing the keyboard — confirmed via a CI page-source dump
    // that no XCUIElementTypeKeyboard element remains once this runs.
    override fun enterPassword(password: String) = passwordField.sendKeys("$password\n")

    override fun tapLogin() {
        // The button is below the login form's scroll view fold — a CI page-source dump
        // showed it at visible="false" with y=676 while the scroll view's own visible frame
        // ends at y=674 — so scroll it into view before tapping. `mobile: scroll` (both
        // element-targeted "toVisible", which fails with WDA's "Failed to find scrollable
        // visible parent with 2 visible children", and a plain "direction" swipe, whether
        // untargeted or scoped to the scroll view) computes its gesture around the target's
        // own bounding-box center, which lands on the password field (the scroll view's
        // vertical center coincides with it) and re-focuses it, popping the keyboard back up
        // over the button — confirmed by repeat CI page-source dumps. Issue a raw W3C swipe
        // with coordinates chosen to fall on blank space above both text fields instead, so
        // the gesture can't touch either one.
        val finger = PointerInput(PointerInput.Kind.TOUCH, "finger")
        val swipeUp = Sequence(finger, 0)
            .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), 196, 235))
            .addAction(finger.createPointerDown(0))
            .addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), 196, 140))
            .addAction(finger.createPointerUp(0))
        iosDriver.perform(listOf(swipeUp))
        iosDriver.findElement(loginButtonLocator).click()
    }
}
