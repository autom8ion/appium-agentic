package dev.appiumagentic.ios

import dev.appiumagentic.pageobject.Screen
import io.appium.java_client.ios.IOSDriver

/** Base class for iOS page objects; adds iOS-specific gestures on top of [Screen]. */
abstract class IosScreen(protected val iosDriver: IOSDriver) : Screen(iosDriver) {

    protected fun hideKeyboard() {
        // The no-arg strategy (tap outside/press "Done") fails with "Did not know how to
        // dismiss the keyboard" on plain UITextFields with no input accessory view and no
        // custom returnKeyType — confirmed against My Demo App iOS 2.2.2's login screen in CI.
        // Pressing the default keyboard's "Return" key is the reliable fallback.
        iosDriver.hideKeyboard("Return")
    }
}
