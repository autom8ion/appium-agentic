package dev.appiumagentic.examples.screens.ios

import dev.appiumagentic.examples.screens.LoginScreen
import dev.appiumagentic.ios.IosScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver

/**
 * NOTE: this environment has no iOS Simulator available to run Appium Inspector against, so
 * these locators were derived by statically inspecting the compiled app bundle (Info.plist and
 * storyboard nib strings) rather than confirmed live. `userNameTF`/`passwordTF` match the
 * IBOutlet property names found in the nib, a common (but unconfirmed) convention for also
 * naming the accessibility identifier. Verify with Appium Inspector against a booted simulator
 * before relying on this in CI, and adjust if they don't match.
 */
class IosLoginScreen(driver: IOSDriver) : IosScreen(driver), LoginScreen {

    private val usernameField by element(AppiumBy.accessibilityId("userNameTF"))
    private val passwordField by element(AppiumBy.accessibilityId("passwordTF"))
    private val loginButton by element(
        AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`label == \"Login\"`]"),
    )

    override fun enterUsername(username: String) = usernameField.sendKeys(username)

    override fun enterPassword(password: String) = passwordField.sendKeys(password)

    override fun tapLogin() = loginButton.click()
}
