package dev.appiumagentic.ios

import dev.appiumagentic.pageobject.Screen
import io.appium.java_client.ios.IOSDriver

/** Base class for iOS page objects; adds iOS-specific gestures on top of [Screen]. */
abstract class IosScreen(protected val iosDriver: IOSDriver) : Screen(iosDriver) {

    protected fun hideKeyboard() {
        iosDriver.hideKeyboard()
    }
}
