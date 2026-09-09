package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.LoginScreen
import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

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

    override fun tapLogin() = iosDriver.findElement(loginButtonLocator).click()

    /**
     * Overrides [LoginScreen]'s default type-then-submit flow: this screen's Login button sits
     * behind the software keyboard once either field has focus, and CI confirmed there's no way
     * to dismiss that keyboard here — no `textFieldShouldReturn`, no `keyboardDismissMode` on
     * the scroll view (so no interactive drag-to-dismiss, which is opt-in per scroll view, not
     * system-wide), and every dismiss technique tried (typed "\n", tapping the real Return key,
     * a raw W3C swipe and XCTest's native `.swipeDown()` over the keyboard, even briefly
     * backgrounding and re-foregrounding the app) left it shown, or iOS restored it on
     * foreground. The scroll view can only move ~59px total (content height 612 vs. frame
     * height 553), nowhere near enough to lift the button above the keyboard's ~230px either.
     *
     * My Demo App iOS ships quick-select buttons for its demo usernames specifically to avoid
     * the keyboard (`LoginViewController.emailButton(_:)` sets both fields' `.text` directly
     * from the tapped button's title, without focusing either field), one of which is this
     * screen's only supported username/password pair. Use it instead of [enterUsername] and
     * [enterPassword]. Only [tapLogin] is shared — with the keyboard never invoked, the button
     * is reachable normally.
     */
    override fun login(username: String, password: String) {
        require(password == "10203040") {
            "IosLoginScreen only supports the demo password (10203040) set by the quick-select " +
                "username buttons — there's no reliable way to type an arbitrary password on " +
                "this screen (see the class doc)."
        }
        iosDriver.findElement(AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == '$username'"))
            .click()
        tapLogin()
    }
}
