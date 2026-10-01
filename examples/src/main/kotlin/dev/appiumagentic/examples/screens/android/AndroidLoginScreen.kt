package dev.appiumagentic.examples.screens.android

import dev.appiumagentic.android.AndroidScreen
import dev.appiumagentic.examples.screens.LoginScreen
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver

private const val PACKAGE = "com.saucelabs.mydemoapp.android"

/** Locators confirmed against My Demo App Android 2.2.0's `fragment_login.xml`. */
class AndroidLoginScreen(driver: AndroidDriver) : AndroidScreen(driver), LoginScreen {

    // The username/password fields have no contentDescription in this app, only a
    // resource-id, so accessibilityId isn't available here — androidUIAutomator/id is the
    // documented exception, not androidUIAutomator/id used as a first resort.
    private val usernameField by element(AppiumBy.id("$PACKAGE:id/nameET"))
    private val passwordField by element(AppiumBy.id("$PACKAGE:id/passwordET"))

    // The login button does have a contentDescription, so accessibilityId applies as normal.
    val loginButton by element(AppiumBy.accessibilityId("Tap to login with given credentials"))

    // Neither error TextView has a contentDescription. passwordErrorTV also shows the
    // locked-out-user message (LoginFragment.java:189).
    val usernameError by element(AppiumBy.id("$PACKAGE:id/nameErrorTV"))
    val passwordError by element(AppiumBy.id("$PACKAGE:id/passwordErrorTV"))

    override fun enterUsername(username: String) = usernameField.sendKeys(username)

    override fun enterPassword(password: String) = passwordField.sendKeys(password)

    override fun tapLogin() = loginButton.click()
}
