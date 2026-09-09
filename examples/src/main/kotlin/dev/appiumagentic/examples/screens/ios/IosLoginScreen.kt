package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.core.wait.Waits
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

    // A trailing "\n" simulates tapping the keyboard's Return key (documented XCTest
    // `typeText:` behavior), dismissing the keyboard.
    override fun enterPassword(password: String) = passwordField.sendKeys("$password\n")

    override fun tapLogin() {
        // The Return-key dismiss above is asynchronous — CI page-source dumps caught the
        // keyboard both fully gone and fully back up moments later from otherwise-identical
        // code, consistent with a dismiss-animation race rather than a wrong element/gesture.
        // Wait for it to actually finish before proceeding: once it has, the button is only
        // clipped by the login form's scroll view fold by a couple of pixels, which a direct
        // click has been enough to reach.
        Waits.until(iosDriver, Duration.ofSeconds(5)) { !iosDriver.isKeyboardShown }
        iosDriver.findElement(loginButtonLocator).click()
    }
}
