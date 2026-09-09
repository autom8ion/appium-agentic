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

    // Confirmed against a CI page-source dump: the software keyboard's Return key is a real
    // XCUIElementTypeButton with accessibility name "Return" (its visible label is the
    // lowercase "return" text on the keycap).
    private val returnKeyLocator = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Return'")

    override fun enterUsername(username: String) = usernameField.sendKeys(username)

    override fun enterPassword(password: String) = passwordField.sendKeys(password)

    override fun tapLogin() {
        // Repeat CI page-source dumps ruled out two dismiss/scroll approaches before this one:
        // a trailing "\n" on the password field (documented XCTest `typeText:` behavior for
        // simulating Return) never reliably dismissed the keyboard here — the dumps kept
        // showing it still up. And `mobile: scroll`, whether element-targeted ("toVisible",
        // which fails outright with WDA's "Failed to find scrollable visible parent with 2
        // visible children") or a plain "direction" swipe, computes its gesture around the
        // target's bounding-box center, which coincides with the password field and
        // re-focuses it, popping the keyboard back up.
        //
        // So: dismiss the keyboard by tapping its real Return key directly (only if it's
        // shown), then scroll the Login button into view — it sits just below the login
        // form's scroll view fold — with a raw W3C swipe using coordinates chosen to land on
        // blank space above both text fields, so the gesture can't touch either one.
        if (iosDriver.isKeyboardShown) {
            iosDriver.findElement(returnKeyLocator).click()
        }
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
