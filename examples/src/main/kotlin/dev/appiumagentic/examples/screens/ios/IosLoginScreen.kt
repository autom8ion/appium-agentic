package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.core.wait.Waits
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

    override fun enterPassword(password: String) = passwordField.sendKeys(password)

    override fun tapLogin() {
        // Neither a trailing "\n" on the password field (documented XCTest `typeText:`
        // behavior for simulating Return) nor tapping the keyboard's real Return key directly
        // reliably dismissed the keyboard here — a follow-up wait for `isKeyboardShown` to go
        // false after either timed out. This app has no in-UI way to dismiss it (no
        // `textFieldShouldReturn`, no `keyboardDismissMode` on the scroll view). What did work,
        // observed in one CI page-source dump, was a swipe gesture that happened to pass
        // through the keyboard's own area — consistent with iOS's system-wide "drag down on
        // the keyboard to dismiss" gesture, which works regardless of app support. Trigger
        // that deliberately: a downward swipe with both endpoints within the keyboard's own
        // bounds. Once it's confirmed gone, the button is only clipped by the login form's
        // scroll view fold by a couple of pixels, small enough for a direct click.
        if (iosDriver.isKeyboardShown) {
            val finger = PointerInput(PointerInput.Kind.TOUCH, "finger")
            val dismissSwipe = Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), 196, 580))
                .addAction(finger.createPointerDown(0))
                .addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), 196, 780))
                .addAction(finger.createPointerUp(0))
            iosDriver.perform(listOf(dismissSwipe))
            Waits.until(iosDriver, Duration.ofSeconds(5)) { !iosDriver.isKeyboardShown }
        }
        iosDriver.findElement(loginButtonLocator).click()
    }
}
